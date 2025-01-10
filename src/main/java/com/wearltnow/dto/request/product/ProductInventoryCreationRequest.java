package com.wearltnow.dto.request.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInventoryCreationRequest {

    @NotNull(message = "product-inventory-product-id.notnull")
    private Long productId;

    @NotNull(message = "product-inventory-color.notnull")
    private String color;

    @NotNull(message = "product-inventory-size.notnull")
    private String size;

    @NotNull(message = "product-inventory-quantity.notnull")
    private Integer quantity;

    @NotNull(message = "product-inventory-purchase-price.notnull")
    private BigDecimal purchasePrice;
}
