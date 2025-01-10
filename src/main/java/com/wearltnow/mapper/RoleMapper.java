package com.wearltnow.mapper;

import com.wearltnow.dto.request.auth.RoleRequest;
import com.wearltnow.dto.request.auth.UserRoleRequest;
import com.wearltnow.dto.response.auth.RoleResponse;
import com.wearltnow.dto.response.auth.UserRoleResponse;
import com.wearltnow.model.Role;
import com.wearltnow.model.User;
import com.wearltnow.model.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
    @Mapping(target = "id.userUserId", source = "request.userId")
    @Mapping(target = "id.rolesName", source = "request.name")
    UserRole toUserRole(UserRoleRequest request, User user, Role role);

    @Mapping(target = "userUserId", source = "user.userId")
    @Mapping(target = "rolesName", source = "role.name")
    UserRoleResponse toUserRoleResponse(UserRole userRole);  // Thêm ở đây
}
