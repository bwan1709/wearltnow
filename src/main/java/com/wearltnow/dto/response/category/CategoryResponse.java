package com.wearltnow.dto.response.category;

import com.wearltnow.dto.response.supplier.SupplierResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    private Long categoryId;
    private String name;
    private String image;
    private String description;
    private Boolean status;
    private String slug;
    private Long parentId;
    private List<SubCategoryResponse> subCategories;
    private SupplierResponse supplier;
}
