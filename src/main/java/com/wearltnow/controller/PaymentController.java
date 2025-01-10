package com.wearltnow.controller;

import com.wearltnow.constant.PaymentStatus;
import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.request.payment.PaymentRequest;
import com.wearltnow.dto.response.payment.PaymentResponse;
import com.wearltnow.dto.response.payment.PaymentStatusResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.model.PaymentTypes;
import com.wearltnow.service.PaymentService;
import com.wearltnow.util.MessageUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MessageUtils messageUtils;

    @PostMapping("/createQR")
    public ApiResponse<PaymentResponse> createQR(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.createPayment(paymentRequest.getOrderId());
        System.out.println("QR code generated for Payment ID: " + response.getPaymentId());

        paymentService.startPaymentCheck(response.getPaymentId());

        // Trả về API response
        return ApiResponse.<PaymentResponse>builder()
                .result(response)
                .build();
    }


    @GetMapping("/{paymentId}/status")
    public ApiResponse<PaymentStatusResponse> checkPaymentStatus(@PathVariable Long paymentId) {
        try {
            PaymentStatusResponse statusResponse = paymentService.getPaymentStatus(paymentId);
            LocalDateTime paymentTime = statusResponse.getPaymentTime() != null ? statusResponse.getPaymentTime() : null;

            // Truyền đủ 4 tham số vào PaymentStatusResponse
            return ApiResponse.<PaymentStatusResponse>builder()
                    .result(new PaymentStatusResponse(
                    statusResponse.getPaymentStatus(),
                    statusResponse.getCountdownTime(),
                    statusResponse.getCreatedAt(),
                    paymentTime  // Thêm thời gian thanh toán (hoặc null)
            )).build();
        } catch (AppException e) {
            return ApiResponse.<PaymentStatusResponse>builder()
                    .result(new PaymentStatusResponse(PaymentStatus.UNPAID, e.getMessage(), LocalDateTime.now(), null))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<PaymentStatusResponse>builder()
                    .result(new PaymentStatusResponse(PaymentStatus.UNPAID, "Error occurred while checking payment status", LocalDateTime.now(), null))
                    .build();
        }
    }

    @GetMapping("/payment-types")
    public ApiResponse<List<PaymentTypes>> getAllPaymentTypes() {
        return ApiResponse.<List<PaymentTypes>>builder()
                .result(paymentService.getPaymentTypes())
                .build();
    }

    @PutMapping("/status/{paymentId}")
    public ApiResponse<String> updatePaymentStatus(@PathVariable Long paymentId,@RequestParam String paymentStatus) {
        try {
            // Validate payment status (ví dụ: chỉ cho phép một số trạng thái nhất định)
            if (!paymentService.isValidPaymentStatus(paymentStatus)) {
                return ApiResponse.<String>builder()
                        .message("Invalid Payment Status")
                        .build();
            }
            paymentService.updatePaymentStatus(paymentId, paymentStatus);
            return ApiResponse.<String>builder()
                    .message("Successfully updated Payment Status")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .message(e.getMessage())
                    .build();
        }
    }
}
