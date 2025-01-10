package com.wearltnow.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wearltnow.constant.PredefinedRole;
import com.wearltnow.dto.request.auth.*;
import com.wearltnow.dto.response.auth.AuthenticationResponse;
import com.wearltnow.dto.response.auth.IntrospectResponse;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.UserMapper;
import com.wearltnow.model.InvalidatedToken;
import com.wearltnow.model.Role;
import com.wearltnow.model.User;
import com.wearltnow.repository.InvalidatedRepository;
import com.wearltnow.repository.RoleRepository;
import com.wearltnow.repository.UserGroupRepository;
import com.wearltnow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedRepository invalidatedRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    EmailService emailService;
    private final OtpStorage otpStorage;
    private final RegisterService registerService;
    private final UserGroupRepository userGroupRepository;
    @NonFinal
    @Value("${signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${valid-duration}")
    protected Long validDuration;

    @NonFinal
    @Value("${refreshable-duration}")
    protected Long refreshDuration;

    public AuthenticationResponse authenticated(AuthenticationRequest authenticationRequest) {
        var user = userRepository.findByUsernameAndDeletedFalse(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        UserResponse userResponse = userMapper.toUserResponse(user);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        Instant expirationInstant = Instant.now().plus(validDuration, ChronoUnit.SECONDS);
        String formattedExpirationTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.ofHours(7))
                .format(expirationInstant);

        if(!authenticated) {
            throw new AppException(ErrorCode.USERNAME_PASSWORD_INCORRECT);
        }
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .user(userResponse)
                .expires(formattedExpirationTime)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try{
           verifyToken(token, false);
        }catch(AppException e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }


        String otpCode = generateCode();
        emailService.sendOtpEmail(request.getEmail(), otpCode);
        otpStorage.saveOtp(request.getEmail(), otpCode);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(request.getUsername());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setPassword(request.getPassword());

        registerService.saveRegisterRequest(registerRequest);
    }


    public void verifyOtp(OtpRequest otpRequest) {
        boolean isValid = otpStorage.validateOtp(otpRequest.getEmail(), otpRequest.getOtp());
        if (!isValid) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        RegisterRequest registerRequest = registerService
                .findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.REGISTRATION_NOT_FOUND));

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Role customerRole = roleRepository.findByName(PredefinedRole.CUSTOMER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(customerRole);

        userRepository.save(user);

        registerService.removeRegisterRequest(otpRequest.getEmail());
        otpStorage.deleteOtp(otpRequest.getEmail());
    }

    public void forgotPassword (ForgotPasswordRequest request){
        userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        String otpCode = generateCode();
        emailService.sendOtpEmail(request.getEmail(), otpCode);
        otpStorage.saveOtp(request.getEmail(), otpCode);
    }

    public void verifyForgotPasswordOtp(OtpForgotPasswordRequest otpForgotPasswordRequest) {
        boolean isValid = otpStorage.validateOtp(otpForgotPasswordRequest.getEmail(), otpForgotPasswordRequest.getOtp());
        if (!isValid) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        User user = userRepository.findByEmail(otpForgotPasswordRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(otpForgotPasswordRequest.getNewPassword()));
        userRepository.save(user);

        otpStorage.deleteOtp(otpForgotPasswordRequest.getEmail());
    }



    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
            var signJWT = verifyToken(request.getToken(), true);

            var jit = signJWT.getJWTClaimsSet().getJWTID();
            var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedRepository.save(invalidatedToken);

        var username = signJWT.getJWTClaimsSet().getSubject();


        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .expires(expiryTime.toString())
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedRepository.save(invalidatedToken);
        } catch (AppException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    SignedJWT verifyToken (String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiration = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(refreshDuration, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if(!(verified && expiration.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if(invalidatedRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    public static String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date expirationTime = new Date(Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli());


        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("wearltnow.com")
                .issueTime(new Date())
                .expirationTime(expirationTime)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create JWT token ", e);
            throw new RuntimeException(e);
        }
    }

    public String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }
        return stringJoiner.toString();
    }

}
