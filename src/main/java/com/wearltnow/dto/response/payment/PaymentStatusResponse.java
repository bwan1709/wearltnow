package com.wearltnow.dto.response.payment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentStatusResponse {
    private String paymentStatus; // PAID hoặc UNPAID
    private String countdownTime; // Thời gian còn lại
    private LocalDateTime paymentTime; // Thay đổi từ LocalDate sang LocalDateTime
    private LocalDateTime createdAt; // Thay đổi từ LocalDate sang LocalDateTime

    public PaymentStatusResponse() {
    }

    public PaymentStatusResponse(String paymentStatus, String countdownTime, LocalDateTime createdAt, LocalDateTime paymentTime) {
        this.paymentStatus = paymentStatus;
        this.countdownTime = countdownTime;
        this.createdAt = createdAt;
        this.paymentTime = paymentTime;
    }

}
