package com.wearltnow.controller;

import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.user.UserCreationRequest;
import com.wearltnow.dto.request.user.UserUpdatePasswordRequest;
import com.wearltnow.dto.request.user.UserUpdateRequest;
import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping()
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping()
    ApiResponse<PageResponse<UserResponse>> getAllUsers
            (@RequestParam(value = "page", required = false, defaultValue = "1") int page,
             @RequestParam(value = "size", required = false, defaultValue = "8") int size) {
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.getAllUsers(page,size))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUser(@PathVariable("id") Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(id))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest request, @PathVariable("id") Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(id, request))
                .build();
    }

    @PutMapping("/change-password/{id}")
    ApiResponse<UserResponse> updateUserPassword(@RequestBody @Valid UserUpdatePasswordRequest request, @PathVariable("id") Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserPassword(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .build();
    }
}
