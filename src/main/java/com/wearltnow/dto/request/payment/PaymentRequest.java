package com.wearltnow.dto.request.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId; // Thuộc tính orderId
    private BigDecimal totalAmount; // Ví dụ về một thuộc tính khác

}
