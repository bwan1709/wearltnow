package com.wearltnow.dto.response.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private int quantity;
    private String size;
    private String color;
    private String name;
    private double price;
    private String image;
}
