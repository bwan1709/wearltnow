package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends AbstractModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long paymentId;

    BigDecimal amount;
    String paymentStatus;
    LocalDateTime paymentTime;

    @OneToOne(mappedBy = "payment")
    Order order;

    @OneToOne
    @JoinColumn(name = "payment_type_id", referencedColumnName = "paymentTypeId")
    PaymentTypes paymentType;
}