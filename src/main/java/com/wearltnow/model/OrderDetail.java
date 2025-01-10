package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail extends AbstractModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderDetailId;

    BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    String color;
    String size;
    int quantity;
}


