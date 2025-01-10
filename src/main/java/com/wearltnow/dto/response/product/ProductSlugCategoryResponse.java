package com.wearltnow.dto.response.product;

import com.wearltnow.dto.response.category.CategoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSlugCategoryResponse {
    private Long productId;
    private String name;
    private Double price;
    private String image;
    private String description;
    private Boolean isActive;
    private CategoryResponse category;

    List<ProductInventoryResponse> inventories;
}
