package com.wearltnow.repository;

import com.wearltnow.model.UserRole;
import com.wearltnow.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUserUserId(Long userId);
}
