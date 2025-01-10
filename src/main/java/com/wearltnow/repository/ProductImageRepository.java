package com.wearltnow.repository;

import com.wearltnow.model.Product;
import com.wearltnow.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository  extends JpaRepository<ProductImage, Long> {


    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId")
    List<ProductImage> findByProductId(@Param("productId") Long productId);

}
