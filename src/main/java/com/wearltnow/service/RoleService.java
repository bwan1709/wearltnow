package com.wearltnow.service;

import com.wearltnow.dto.request.auth.RoleRequest;
import com.wearltnow.dto.request.auth.UserRoleRequest;
import com.wearltnow.dto.response.auth.RoleResponse;
import com.wearltnow.dto.response.auth.UserRoleResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.RoleMapper;
import com.wearltnow.repository.PermissionRepository;
import com.wearltnow.repository.RoleRepository;
import com.wearltnow.repository.UserRepository;
import com.wearltnow.repository.UserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
   RoleRepository roleRepository;
   RoleMapper roleMapper;
   PermissionRepository permissionRepository;
   UserRepository userRepository;
   UserRoleRepository userRoleRepository;

    @PreAuthorize("hasRole('DIRECTOR')")
    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @PreAuthorize("hasRole('DIRECTOR')")
    public UserRoleResponse createUserRole(UserRoleRequest request) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        var role = roleRepository.findById(request.getName())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // Tạo đối tượng UserRoleId để lưu khóa chính hỗn hợp
        var userRole = roleMapper.toUserRole(request, user, role);

        // Lưu vào bảng users_roles
        userRoleRepository.save(userRole);

        // Trả về phản hồi dạng UserRoleResponse
        return roleMapper.toUserRoleResponse(userRole);


    }

    @PreAuthorize("hasRole('DIRECTOR')")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }
    @PreAuthorize("hasRole('DIRECTOR')")
    public void delete(String id) {
        roleRepository.deleteById(id);
    }
    }
