package com.wearltnow.repository;

import com.wearltnow.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Query("SELECT p FROM Product p JOIN p.category c LEFT JOIN c.parentCategory parent "
            + "WHERE (c.slug LIKE %:slug% OR parent.slug LIKE %:slug%) AND p.deleted = false")
    List<Product> findByCategorySlug(@Param("slug") String slug);
    Page<Product> findAllByDeletedFalse(Pageable pageable);
    Optional<Product> findByProductIdAndDeletedFalse(Long productId);
}
