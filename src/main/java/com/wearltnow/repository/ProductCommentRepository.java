package com.wearltnow.repository;

import com.wearltnow.model.ProductComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    @Query("SELECT AVG(pc.rate) FROM ProductComment pc WHERE pc.product.productId = :productId")
    Double findAverageRateByProductId(@Param("productId") Long productId);

    List<ProductComment> findByProduct_ProductId(Long productId);
}

