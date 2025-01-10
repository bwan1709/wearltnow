package com.wearltnow.service;

import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.UserGroup;
import com.wearltnow.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public UserGroup createUserGroup(String name) {
        if (userGroupRepository.findByName(name).isPresent()) {
            throw new AppException(ErrorCode.USER_GROUP_EXISTS);  // Nếu đã có thì ném lỗi
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setName(name);
        return userGroupRepository.save(userGroup);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public UserGroup updateUserGroup(Long id, String name) {
        UserGroup userGroup = userGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("UserGroup not found"));

        userGroup.setName(name);
        userGroup.setUpdatedAt(LocalDateTime.now());
        return userGroupRepository.save(userGroup);
    }
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void deleteUserGroup(Long id) {
        UserGroup userGroup = userGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("UserGroup not found"));
        // Đánh dấu là đã xóa
        userGroupRepository.delete(userGroup);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<UserGroup> getAllActiveUserGroups() {
        return userGroupRepository.findByDeletedFalse();
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public Optional<UserGroup> getUserGroupByName(String name) {
        return userGroupRepository.findByName(name);
    }
}
