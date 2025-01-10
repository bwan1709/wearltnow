package com.wearltnow.service;

import com.wearltnow.dto.request.auth.PermissionRequest;
import com.wearltnow.dto.response.auth.PermissionResponse;
import com.wearltnow.mapper.PermissionMapper;
import com.wearltnow.model.Permission;
import com.wearltnow.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<PermissionResponse> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}
