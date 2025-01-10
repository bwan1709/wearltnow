package com.wearltnow.mapper;

import com.wearltnow.dto.request.product.ProductInventoryCreationRequest;
import com.wearltnow.dto.request.product.ProductInventoryUpdationRequest;
import com.wearltnow.dto.response.product.ProductInventoryResponse;
import com.wearltnow.model.ProductInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductInventoryMapper {
    ProductInventory toProductInventory(ProductInventoryCreationRequest request);
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(request.getQuantity(), request.getPurchasePrice()))")
    ProductInventoryResponse toProductInventoryResponse(ProductInventory request);
    void updateProductInventory(@MappingTarget ProductInventory productInventory, ProductInventoryUpdationRequest request);

    default BigDecimal calculateTotalPrice(Integer quantity, BigDecimal purchasePrice) {
        if (quantity == null || purchasePrice == null) {
            return BigDecimal.ZERO;
        }
        return purchasePrice.multiply(BigDecimal.valueOf(quantity));
    }
}
