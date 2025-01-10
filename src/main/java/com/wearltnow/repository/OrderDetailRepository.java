package com.wearltnow.repository;

import com.wearltnow.dto.response.statistic.ProductLeastSellResponse;
import com.wearltnow.dto.response.statistic.ProductStatsResponse;
import com.wearltnow.model.OrderDetail;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT new com.wearltnow.dto.response.statistic.ProductStatsResponse(od.product.name, SUM(od.quantity), SUM(CAST(od.quantity * od.price AS double)))" +
            "FROM OrderDetail od " +
            "WHERE od.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY od.product.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<ProductStatsResponse> findProductSalesStatsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.wearltnow.dto.response.statistic.ProductStatsResponse(od.product.name, SUM(od.quantity), SUM(CAST(od.quantity * od.price AS double))) " +
            "FROM OrderDetail od " +
            "GROUP BY od.product.name " +
            "ORDER BY SUM(od.quantity) DESC")
    List<ProductStatsResponse> findProductSalesStats();

    @Query("SELECT new com.wearltnow.dto.response.statistic.ProductLeastSellResponse(p.productId, p.name, " +
            "COALESCE(CAST(SUM(od.quantity) AS long), 0L), " + // Chuyển đổi sang Long
            "COALESCE(CAST(SUM(od.price * od.quantity) AS double), 0.0)) " + // Chuyển đổi sang Double
            "FROM ProductInventory pi " +
            "JOIN pi.product p " +
            "LEFT JOIN OrderDetail od ON p.productId = od.product.productId AND od.createdAt >= :thirtyDaysAgo " +
            "WHERE pi.quantity > 0 " +
            "GROUP BY p.productId, p.name " +
            "ORDER BY COALESCE(CAST(SUM(od.quantity) AS long), 0L) ASC")
    List<ProductLeastSellResponse> findLeastSellingProductsInStock(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
}
