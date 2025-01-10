package com.wearltnow.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "product_inventories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInventory extends AbstractModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productInventoryId;

    String color;
    String size;
    Integer quantity;

    @Column(name = "purchase_price", nullable = false)
    BigDecimal purchasePrice;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;
}
