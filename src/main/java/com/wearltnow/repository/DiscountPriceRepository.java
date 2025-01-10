package com.wearltnow.repository;

import com.wearltnow.model.DiscountPrice;
import com.wearltnow.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountPriceRepository extends JpaRepository<DiscountPrice, Long> {
    Page<DiscountPrice> findAllByDeletedFalse(Pageable pageable);
    Optional<DiscountPrice> findByIdAndDeletedFalse(Long id);
    Optional<DiscountPrice> findByProductAndDeletedFalse(Product product);
}
