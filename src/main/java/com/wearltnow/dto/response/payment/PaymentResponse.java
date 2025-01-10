package com.wearltnow.dto.response.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long paymentId;
    private BigDecimal amount;
    private String paymentStatus;
    private LocalDateTime createdAt; // Thay đổi từ LocalDate sang LocalDateTime
    private String qrCode;
    private String countdownTime;

    public PaymentResponse() {
    }

    public PaymentResponse(String qrCode) {
        this.qrCode = qrCode;
    }
}
