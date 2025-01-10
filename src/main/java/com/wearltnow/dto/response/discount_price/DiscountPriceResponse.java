package com.wearltnow.dto.response.discount_price;

import com.wearltnow.dto.response.product.ProductResponse;
import com.wearltnow.model.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DiscountPriceResponse {
    private Long id;

    private Long productId;

    private ProductResponse product;

    private Double discountRate;

    private LocalDate startDate;

    private LocalDate endDate;
}

