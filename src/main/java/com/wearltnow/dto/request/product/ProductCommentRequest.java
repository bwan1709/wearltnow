package com.wearltnow.dto.request.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCommentRequest {

    @NotNull(message = "product-id.notnull")
    private Long productId;
    @NotNull(message = "userId.notnull")
    private Long userId;
    @NotBlank()
    @NotNull(message = "review-content.notnull")
    private String content;

    @NotNull(message = "review-rate.notnull")
    @Min(value = 1, message = "review-rate.min")
    @Max(value = 5, message = "review-rate.max")
    private Integer rate;
    private Long parentId;

}
