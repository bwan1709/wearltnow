package com.wearltnow.repository;

import com.wearltnow.model.PaymentTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentTypes, Long> {
    PaymentTypes findByPaymentTypeId(long id);
}
