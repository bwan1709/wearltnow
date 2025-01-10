package com.wearltnow.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "cart_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long cartItemId;

    private Long productId;
    private int quantity;
    private String size;
    private String color;

    public CartItem(Long productId, int quantity, String size, String color) {
        this.productId = productId;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }
}
