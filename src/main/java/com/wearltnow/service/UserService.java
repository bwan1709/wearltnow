package com.wearltnow.service;

import com.wearltnow.constant.PredefinedRole;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.user.UserCreationRequest;
import com.wearltnow.dto.request.user.UserUpdatePasswordRequest;
import com.wearltnow.dto.request.user.UserUpdateRequest;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.UserMapper;
import com.wearltnow.model.Role;
import com.wearltnow.model.User;
import com.wearltnow.model.UserGroup;
import com.wearltnow.repository.RoleRepository;
import com.wearltnow.repository.UserGroupRepository;
import com.wearltnow.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    private final UserGroupRepository userGroupRepository;

    // PreAuthorize cho phép phân quyền, sẽ kiểm tra quyền user trước khi chấp nhận truy cập method
    // Giả sử bạn muốn phân quyền theo role, sử dụng hasRole(), theo permission, sử dụng hasAuthority()
    // Check role + permission trực tiếp trong database
    // PreAuthorize sẽ check trước khi call method, PostAuthorize sẽ call method trước khi check quyền user

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    // @PreAuthorize("hasAuthority('UPDATE_DATA')")
    public PageResponse<UserResponse> getAllUsers(int page, int size){
        Sort sort = Sort.by(Sort.Direction.ASC, "userId");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = userRepository.findAllByDeletedFalse(pageable);
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(userMapper::toUserResponse).toList())
                .build();
    }
    // Nếu username của UserResponse trả về == với username đang đăng nhập thì cho phép truy cập
    // @PostAuthorize("returnObject.username == authentication.name")
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public UserResponse getUserById(Long id){return userMapper.toUserResponse(userRepository.findByUserIdAndDeletedFalse(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        UserGroup userGroup = userGroupRepository.findByName(com.wearltnow.constant.UserGroup.STAFF).get();
        // Tìm role 'CUSTOMER' từ bảng roles
        Role customerRole = roleRepository.findByName(PredefinedRole.STAFF_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // Thêm vai trò 'CUSTOMER' vào người dùng
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(customerRole);
        user.setUserGroup(userGroup);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("hasRole('DIRECTOR') or hasRole('STAFF') or returnObject.username == authentication.name")
    public UserResponse updateUser(Long id, UserUpdateRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        userRepository.findByEmail(request.getEmail())
                .filter(existingUser -> !existingUser.getUserId().equals(id))
                .ifPresent(existingUser -> {
                    throw new AppException(ErrorCode.EMAIL_EXISTED);
                });
        userMapper.updateUser(user, request);

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void deleteUser(Long id){
        userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        userRepository.deleteById(id);
    }
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public UserResponse updateUserPassword(Long id, UserUpdatePasswordRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public User findAdmin() {
        return userRepository.findByRoles_Name(PredefinedRole.DIRECTOR_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.DIRECTOR_NOTFOUND));
    }
}