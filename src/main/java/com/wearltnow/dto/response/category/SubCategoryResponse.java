package com.wearltnow.dto.response.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubCategoryResponse {
    private Long categoryId;
    private String name;
    private Long parentId;
    private String slug;
    private List<SubCategoryResponse> subCategories;
}
