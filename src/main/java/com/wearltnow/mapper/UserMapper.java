package com.wearltnow.mapper;

import com.wearltnow.dto.request.auth.RegisterRequest;
import com.wearltnow.dto.request.user.UserCreationRequest;
import com.wearltnow.dto.request.user.UserUpdateRequest;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    User toUserRegister(RegisterRequest request);
}
