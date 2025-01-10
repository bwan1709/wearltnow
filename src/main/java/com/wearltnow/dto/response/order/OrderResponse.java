package com.wearltnow.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long paymentId;
    private Long userId;
    private String order_code;
    private BigDecimal totalOrder;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private String status;
    private String payment_status;
    private String expected_delivery_time;
    private LocalDateTime createdAt;
    private List<OrderDetailResponse> orderDetails;

}
