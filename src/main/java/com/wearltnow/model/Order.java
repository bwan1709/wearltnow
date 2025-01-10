package com.wearltnow.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends AbstractModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;

    BigDecimal orderAmount;

    BigDecimal orderTotal;
    BigDecimal shippingFee;
    BigDecimal discountAmount = BigDecimal.ZERO;

    String order_code;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id")
    User user;

    String status;
    String expected_delivery_time;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payment_id")
    Payment payment;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;


}
