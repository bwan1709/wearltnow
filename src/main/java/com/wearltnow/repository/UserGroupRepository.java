package com.wearltnow.repository;

import com.wearltnow.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    // Tìm nhóm người dùng theo tên
    Optional<UserGroup> findByName(String name);

    // Tìm nhóm người dùng theo id và trạng thái chưa bị xóa
    Optional<UserGroup> findByIdAndDeletedFalse(Long id);

    // Tìm tất cả nhóm người dùng chưa bị xóa
    List<UserGroup> findByDeletedFalse();
}
