package com.wearltnow.dto.request.category;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreationRequest {
     @NotNull(message = "category-name.notnull")
     String name;

     MultipartFile image;

     String description;

     @NotNull(message = "category-status.notnull")
     Boolean status;

     String slug;

     Long parentId;

     @NotNull(message = "category-supplier-id.notnull")
     Long supplierId;

}
