package com.wearltnow.mapper;

import com.wearltnow.dto.request.user.UserAddressGroupCreationRequest;
import com.wearltnow.dto.request.user.UserAddressGroupUpdationRequest;
import com.wearltnow.dto.response.user.UserAddressGroupResponse;
import com.wearltnow.model.UserAddressGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAddressGroupMapper {

    @Mapping(target = "user.userId", source = "userId")
    UserAddressGroup toUserAddressGroup(UserAddressGroupCreationRequest userAddressGroupCreationRequest);
    @Mapping(target = "userId", source = "user.userId")

    UserAddressGroupResponse toUserAddressGroupResponse(UserAddressGroup userAddressGroup);
    List<UserAddressGroupResponse> toUserAddressGroupResponseList(List<UserAddressGroup> userAddressGroups);
    @Mapping(target = "user.userId", source = "userId") // Nếu bạn muốn đưa userId vào response
    void updateUserAddressGroup(@MappingTarget UserAddressGroup userAddressGroup, UserAddressGroupUpdationRequest request);
}
