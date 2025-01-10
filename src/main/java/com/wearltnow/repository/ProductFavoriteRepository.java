package com.wearltnow.repository;

import com.wearltnow.model.Product;
import com.wearltnow.model.ProductFavorite;
import com.wearltnow.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Long> {
    Optional<ProductFavorite> findByUserAndProduct(User user, Product product);
    List<ProductFavorite> findByUser(User user);
    void deleteByUserAndProduct(User user, Product product);
    @Query("SELECT COUNT(pf) FROM ProductFavorite pf WHERE pf.product.productId = :productId")
    Long countFavoritesByProductId(@Param("productId") Long productId);

}
