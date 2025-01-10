package com.wearltnow.specification;

import com.wearltnow.model.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {
    public static Specification<Category> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("deleted"));
    }
    public static Specification<Category> hasName(String categoryName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + categoryName.toLowerCase() + "%");
    }
    public static Specification<Category> hasSupplier(String supplierName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("supplier").get("description")), "%" + supplierName.toLowerCase() + "%");
    }

}
