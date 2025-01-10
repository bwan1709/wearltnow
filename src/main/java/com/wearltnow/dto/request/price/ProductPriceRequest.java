package com.wearltnow.dto.request.price;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceRequest {
    Long productId;
    Long productPriceId;
    Double priceValue;
}

