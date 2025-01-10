package com.wearltnow.controller;

import com.wearltnow.dto.request.auth.RoleRequest;
import com.wearltnow.dto.request.auth.UserRoleRequest;
import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.auth.RoleResponse;
import com.wearltnow.dto.response.auth.UserRoleResponse;
import com.wearltnow.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping("/permission_roles")
    ApiResponse<RoleResponse> createRolePermission(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @PostMapping("user_roles")
    ApiResponse<UserRoleResponse> createUserRole(@RequestBody UserRoleRequest request) {
        return ApiResponse.<UserRoleResponse>builder()
                .result(roleService.createUserRole(request))
                .build();
    }

    @GetMapping("/roles")
    ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/roles/{role}")
    ApiResponse<Void> delete(@PathVariable() String role) {
        roleService.delete(role);
        return ApiResponse.<Void>builder().build();
    }
}
