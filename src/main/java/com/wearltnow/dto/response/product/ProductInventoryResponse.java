package com.wearltnow.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInventoryResponse {
    Long productInventoryId;
    Long productId;
    String color;
    String size;
    Integer quantity;
    BigDecimal purchasePrice;
    BigDecimal totalPrice;
}
