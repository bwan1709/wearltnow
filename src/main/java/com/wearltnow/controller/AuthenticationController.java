package com.wearltnow.controller;

import com.nimbusds.jose.JOSEException;
import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.request.auth.*;
import com.wearltnow.dto.response.auth.AuthenticationResponse;
import com.wearltnow.dto.response.auth.IntrospectResponse;
import com.wearltnow.service.AuthenticationService;
import com.wearltnow.service.OtpStorage;
import com.wearltnow.util.MessageUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    MessageUtils messageUtils;
    private final OtpStorage otpStorage;


    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        var result = authenticationService.authenticated(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message(messageUtils.getAttributeMessage("login.success"))
                .result(result)
                .build();
    }

    @PostMapping("/register")
    ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request){
        authenticationService.register(request);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("otp.sent"))
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(@RequestBody ForgotPasswordRequest request){
        authenticationService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("otp.sent"))
                .build();
    }

    @PostMapping("/verify-forgot-password-otp")
    public ApiResponse<String> verifyForgotPasswordOtp(@RequestBody OtpForgotPasswordRequest otpForgotPasswordRequest){
        authenticationService.verifyForgotPasswordOtp(otpForgotPasswordRequest);
        return ApiResponse.<String>builder()
                .message(messageUtils.getAttributeMessage("reset-password.success"))
                .build();
    }


    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@RequestBody OtpRequest otpRequest) {
        authenticationService.verifyOtp(otpRequest);
        return ApiResponse.<String>builder()
                .code(1000)
                .message(messageUtils.getAttributeMessage("register.success"))
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> login(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("logout.success"))
                .build();
    }
}
