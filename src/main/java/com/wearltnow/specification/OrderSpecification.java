package com.wearltnow.specification;

import com.wearltnow.model.Order;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<Order> createdBetween(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay(); // at 00:00:00
        LocalDateTime toDateTime = toDate.atTime(23, 59, 59); // at 23:59:59

        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"), fromDateTime, toDateTime);
    }

    public static Specification<Order> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("userId"), customerId);
    }

    public static Specification<Order> hasProductId(Long productId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.join("orderDetails").get("product").get("productId"), productId
        );
    }

    public static Specification<Order> hasOrderCode(String orderCode) {
        return (root, query, criteriaBuilder) -> {
            if (orderCode == null || orderCode.isEmpty()) {
                return criteriaBuilder.conjunction(); // No filtering applied if orderCode is null or empty
            }
            return criteriaBuilder.like(root.get("order_code"), "%" + orderCode + "%");
        };
    }


    // Bộ lọc theo trạng thái đơn hàng
    public static Specification<Order> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null || status.isEmpty()) {
                return criteriaBuilder.conjunction(); // Không lọc nếu trạng thái rỗng
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    // Bộ lọc theo tổng tiền (orderTotal)
    public static Specification<Order> hasTotalAmountGreaterThanOrEqual(BigDecimal totalAmount) {
        return (root, query, criteriaBuilder) -> {
            if (totalAmount == null) {
                return criteriaBuilder.conjunction(); // Không lọc nếu tổng tiền không được chỉ định
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("orderTotal"), totalAmount);
        };
    }

    public static Specification<Order> hasTotalAmountLessThanOrEqual(BigDecimal totalAmount) {
        return (root, query, criteriaBuilder) -> {
            if (totalAmount == null) {
                return criteriaBuilder.conjunction(); // Không lọc nếu tổng tiền không được chỉ định
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("orderTotal"), totalAmount);
        };
    }
}
