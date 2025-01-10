package com.wearltnow.dto.response.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String color;
    private String size;
    private String image;
}
