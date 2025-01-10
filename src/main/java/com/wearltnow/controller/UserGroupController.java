package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.user.UserGroupResponse;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.UserGroup;
import com.wearltnow.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-groups")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;

    // Tạo nhóm người dùng mới
    @PostMapping("/create")
    public ApiResponse<UserGroup> createUserGroup(@RequestParam String name) {
        UserGroup userGroup = userGroupService.createUserGroup(name);
        return ApiResponse.<UserGroup>builder()
                .message("User group created successfully")
                .result(userGroup)
                .build();
    }

    // Cập nhật nhóm người dùng
    @PutMapping("/update/{id}")
    public ApiResponse<Void> updateUserGroup(@PathVariable Long id, @RequestParam String name) {
        userGroupService.updateUserGroup(id, name);
        return ApiResponse.<Void>builder()
                .message("User group updated successfully")
                .build();
    }

    // Xóa nhóm người dùng (soft delete)
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteUserGroup(@PathVariable Long id) {
        userGroupService.deleteUserGroup(id);
        return ApiResponse.<Void>builder()
                .message("User group deleted successfully")
                .build();
    }

    // Lấy tất cả nhóm người dùng chưa bị xóa
    @GetMapping("/all")
    public ApiResponse<List<UserGroupResponse>> getAllActiveUserGroups() {
        List<UserGroupResponse> userGroups = userGroupService.getAllActiveUserGroups()
                .stream()
                .map(userGroup -> {
                    UserGroupResponse response = new UserGroupResponse();
                    response.setId(userGroup.getId());
                    response.setName(userGroup.getName());
                    response.setUsers(userGroup.getUsers().stream().map(user -> {
                        UserResponse userResponse = new UserResponse();
                        userResponse.setUserId(user.getUserId());
                        userResponse.setUsername(user.getUsername());
                        return userResponse;
                    }).collect(Collectors.toList()));
                    return response;
                })
                .collect(Collectors.toList());

        return ApiResponse.<List<UserGroupResponse>>builder()
                .message("Fetched all active user groups")
                .result(userGroups)
                .build();
    }

    // Lấy nhóm người dùng theo tên
    @GetMapping("/by-name")
    public ApiResponse<UserGroupResponse> getUserGroupByName(@RequestParam String name) {
        UserGroup userGroup = userGroupService.getUserGroupByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_GROUP_NOTFOUND));

        // Mapping UserGroup sang UserGroupResponse
        UserGroupResponse response = new UserGroupResponse();
        response.setId(userGroup.getId());
        response.setName(userGroup.getName());
        response.setUsers(userGroup.getUsers().stream().map(user -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(user.getUserId());
            userResponse.setUsername(user.getUsername());
            return userResponse;
        }).collect(Collectors.toList()));

        return ApiResponse.<UserGroupResponse>builder()
                .message("Fetched user group by name")
                .result(response)
                .build();
    }

}
