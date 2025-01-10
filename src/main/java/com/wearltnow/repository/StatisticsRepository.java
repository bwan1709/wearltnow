package com.wearltnow.repository;

import com.wearltnow.model.Order;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query("SELECT COUNT(DISTINCT o.user.userId) FROM Order o WHERE (:fromDate IS NULL OR o.createdAt >= :fromDate) AND (:toDate IS NULL OR o.createdAt <= :toDate)")
    Long countDistinctUserId(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT COUNT(DISTINCT u.userId) FROM User u " +
            "JOIN u.roles r " +
            "WHERE (:fromDate IS NULL OR FUNCTION('DATE', u.createdAt) >= :fromDate) " +
            "AND (:toDate IS NULL OR FUNCTION('DATE', u.createdAt) <= :toDate) " +
            "AND r.name = 'CUSTOMER'")
    Long countNewCustomers(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT MIN(o.createdAt) FROM Order o")
    Optional<LocalDate> findEarliestOrderDate();

    @Query("SELECT MAX(o.createdAt) FROM Order o")
    Optional<LocalDate> findLatestOrderDate();

    @Query("SELECT c.name FROM Category c WHERE c.categoryId = :categoryId")
    Optional<String> findCategoryNameById(@Param("categoryId") Long categoryId);
}

