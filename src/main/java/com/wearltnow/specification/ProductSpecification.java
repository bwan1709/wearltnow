package com.wearltnow.specification;

import com.wearltnow.model.DiscountPrice;
import com.wearltnow.model.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductSpecification {

    public static Specification<Product> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
    }

    public static Specification<Product> hasName(String productName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + productName.toLowerCase() + "%");
    }

    public static Specification<Product> inCategory(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("categoryId"), categoryId);
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(Double minPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqualTo(Double maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> hasColor(String color) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true); // Loại bỏ sản phẩm bị lặp
            return criteriaBuilder.equal(root.join("productInventories").get("color"), color);
        };
    }

    public static Specification<Product> hasSize(String productSize) {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.distinct(true); // Loại bỏ sản phẩm bị lặp
            return criteriaBuilder.equal(root.join("productInventories").get("size"), productSize);
        };
    }
    public static Specification<Product> hasDiscount() {
        return (root, query, criteriaBuilder) -> {
            // Lấy thông tin giảm giá từ bảng DiscountPrice
            Join<Product, DiscountPrice> discountJoin = root.join("discountPrices", JoinType.LEFT);

            // Điều kiện giảm giá phải có discountRate và phải trong khoảng thời gian hợp lệ
            LocalDateTime now = LocalDateTime.now();
            Predicate discountCondition = criteriaBuilder.and(
                    criteriaBuilder.isNotNull(discountJoin.get("discountRate")),
                    criteriaBuilder.lessThanOrEqualTo(discountJoin.get("startDate"), now.toLocalDate()),
                    criteriaBuilder.greaterThanOrEqualTo(discountJoin.get("endDate"), now.toLocalDate())
            );
            return discountCondition;
        };
    }






    public static Specification<Product> createdDateBetween(LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) -> {
            // Xử lý thời gian mặc định
            LocalDateTime fromDateTime = (from != null)
                    ? from.atStartOfDay()
                    : LocalDate.now().atStartOfDay();

            LocalDateTime toDateTime = (to != null)
                    ? to.atTime(23, 59, 59)
                    : LocalDate.now().atTime(23, 59, 59);

            // Trả về Specification
            return criteriaBuilder.between(root.get("createdAt"), fromDateTime, toDateTime);
        };
    }

}
