package com.wearltnow.repository;

import com.wearltnow.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByDeletedFalse(Pageable pageable);
    Optional<User> findByUserIdAndDeletedFalse(Long userId);

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndDeletedFalse(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Optional<User> findByRoles_Name(String roleName);
}
