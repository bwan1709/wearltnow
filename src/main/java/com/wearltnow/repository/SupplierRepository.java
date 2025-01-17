package com.wearltnow.repository;

import com.wearltnow.model.Supplier;
import com.wearltnow.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Page<Supplier> findAllByDeletedFalse(Pageable pageable);
    Optional<Supplier> findBySupplierIdAndDeletedFalse(Long id);
}
