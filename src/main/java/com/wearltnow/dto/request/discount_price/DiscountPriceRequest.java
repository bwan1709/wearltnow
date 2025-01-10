package com.wearltnow.dto.request.discount_price;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DiscountPriceRequest {
    private Long productId;

    private Double discountRate;

    private LocalDate startDate;

    private LocalDate endDate;
}
