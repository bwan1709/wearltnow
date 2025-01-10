package com.wearltnow.repository;

import com.wearltnow.model.DiscountCode;
import com.wearltnow.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCode(String code);  // Tìm mã giảm giá theo mã

    // Lấy tất cả các mã giảm giá (có sẵn nhưng thêm vào để rõ ràng hơn)
    Page<DiscountCode> findAllByDeletedFalse(Pageable pageable);
}
