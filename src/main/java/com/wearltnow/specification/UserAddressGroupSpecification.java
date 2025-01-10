package com.wearltnow.specification;

import com.wearltnow.model.Product;
import com.wearltnow.model.UserAddressGroup;
import org.springframework.data.jpa.domain.Specification;

public class UserAddressGroupSpecification {
    public static Specification<UserAddressGroup> isActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isActive"));
    }

    public static Specification<UserAddressGroup> isNotDeleted() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
    }
}
