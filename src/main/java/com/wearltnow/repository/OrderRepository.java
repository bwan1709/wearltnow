package com.wearltnow.repository;

import com.wearltnow.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// extends JpaSpecificationExecutor<Order> de ho tro spec
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findAll(Pageable pageable);
    Page<Order> findByUser_UserId(Long userId, Pageable pageable);
    Optional<Order> findById(Long orderId);
}
