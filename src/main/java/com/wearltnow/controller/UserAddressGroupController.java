package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.request.user.UserAddressGroupCreationRequest;
import com.wearltnow.dto.request.user.UserAddressGroupUpdationRequest;
import com.wearltnow.dto.response.user.UserAddressGroupResponse;
import com.wearltnow.mapper.UserAddressGroupMapper;
import com.wearltnow.model.UserAddressGroup;
import com.wearltnow.service.UserAddressGroupService;
import com.wearltnow.util.MessageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-address")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAddressGroupController {

    UserAddressGroupService userAddressGroupService;
    MessageUtils messageUtils;
    UserAddressGroupMapper userAddressGroupMapper;


    // Lấy tất cả địa chỉ của người dùng
    @GetMapping("/{userId}")
    public ApiResponse<List<UserAddressGroupResponse>> getAddressGroupById(@PathVariable Long userId) {
        List<UserAddressGroupResponse> addressGroups = userAddressGroupService.findByUserId(userId);
        return ApiResponse.<List<UserAddressGroupResponse>>builder()
                .result(addressGroups)
                .build();
    }
    // Lấy địa chỉ đang active
    @GetMapping("is-active/{userId}")
    public ApiResponse<UserAddressGroupResponse> getAddressGroupByIdAndIsActive(@PathVariable Long userId) {
        UserAddressGroupResponse addressGroups = userAddressGroupService.findByUserIdAndIsActive(userId);
        return ApiResponse.<UserAddressGroupResponse>builder()
                .result(addressGroups)
                .build();
    }

    @PostMapping
    public ApiResponse<UserAddressGroupResponse> createAddress(@RequestBody UserAddressGroupCreationRequest userAddressGroup) {
        return ApiResponse.<UserAddressGroupResponse>builder()
                .result(userAddressGroupService.create(userAddressGroup))
                .build();
    }

    // Cập nhật địa chỉ
    @PutMapping("/{id}")
    public ApiResponse<UserAddressGroupResponse> updateAddress(@PathVariable Long id,
                                                               @RequestBody UserAddressGroupUpdationRequest request) {
        return ApiResponse.<UserAddressGroupResponse>builder()
                .result(userAddressGroupService.update(id, request))
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserAddressGroupResponse> updateAddressStatus(@PathVariable Long id, @RequestParam Boolean status) {
        // Tìm địa chỉ theo ID
        UserAddressGroup updatedAddress = userAddressGroupService.updateStatus(id, status);
        UserAddressGroupResponse response = userAddressGroupMapper.toUserAddressGroupResponse(updatedAddress);
        return ApiResponse.<UserAddressGroupResponse>builder()
                .message(messageUtils.getAttributeMessage("updated-user-address-status.success"))
                .result(response)
                .build();
    }

    // Xóa địa chỉ
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Integer id) {
        userAddressGroupService.deleteById(id);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("delete.success"))
                .build();
    }

}
