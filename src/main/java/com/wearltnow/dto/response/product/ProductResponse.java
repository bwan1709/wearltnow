package com.wearltnow.dto.response.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wearltnow.dto.response.category.CategoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {
    Long productId;
    String name;
    Double price;
    Double originalPrice; // Giá gốc (trước giảm)
    Double discountRate; // Phần trăm giảm
    String image;
    String description;
    CategoryResponse category;

    List<ProductImageResponse> images;
    List<ProductInventoryResponse> inventories;
    Double averageRate;
    Long favoriteCount;
}
