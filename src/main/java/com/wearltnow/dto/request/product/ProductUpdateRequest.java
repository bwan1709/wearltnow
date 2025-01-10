package com.wearltnow.dto.request.product;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    @NotNull(message = "product-name.notnull")
    String name;

    @NotNull(message = "product-price.notnull")
    BigDecimal price;

    @NotNull(message = "product-category-id.notnull")
    Long categoryId;
    String description;
    MultipartFile image;
    List<MultipartFile> additionalImages;
}
