package com.wearltnow.mapper;

import com.wearltnow.dto.request.auth.PermissionRequest;
import com.wearltnow.dto.response.auth.PermissionResponse;
import com.wearltnow.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
