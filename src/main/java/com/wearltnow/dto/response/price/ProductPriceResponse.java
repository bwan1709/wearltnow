package com.wearltnow.dto.response.price;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceResponse {
    Long id;
    Long productPriceId;
    String name;
    Long productId;
    Double priceValue;
}

