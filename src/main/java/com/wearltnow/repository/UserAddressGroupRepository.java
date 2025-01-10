package com.wearltnow.repository;

import com.wearltnow.model.UserAddressGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressGroupRepository extends JpaRepository<UserAddressGroup, Integer>, JpaSpecificationExecutor<UserAddressGroup> {
    Optional<List<UserAddressGroup>> findByUser_UserId(Long userId);
    List<UserAddressGroup> findAllByUser_UserIdAndIsActiveTrue(Long userId);
    Optional<UserAddressGroup> findByUserUserIdAndIsActive(Long userId, Boolean isActive);
    Optional<UserAddressGroup> findById(Long id);

}