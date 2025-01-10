package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.user.AddressRequest;
import com.wearltnow.dto.request.order.CheckoutDto;
import com.wearltnow.dto.request.order.CheckoutRequest;
import com.wearltnow.dto.response.order.OrderResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.Order;
import com.wearltnow.service.OrderService;
import com.wearltnow.util.MessageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;
    MessageUtils messageUtils;

    @PostMapping
    public ApiResponse<OrderResponse> checkout(@RequestBody CheckoutDto checkoutDto) {
        CheckoutRequest checkoutRequest = checkoutDto.getCheckoutRequest();
        AddressRequest addressRequest = checkoutDto.getAddressRequest();

        OrderResponse orderResponse = orderService.createOrder(checkoutRequest,
                addressRequest);
        return ApiResponse.<OrderResponse>builder()
                .result(orderResponse)
                .message(messageUtils.getAttributeMessage("checkout.success"))
                .build();
    }
    @GetMapping
    public ApiResponse<PageResponse<OrderResponse>> findAllOrders(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size,
            @RequestParam(value = "orderCode", required = false) String orderCode,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "minTotalAmount", required = false) BigDecimal minTotalAmount,
            @RequestParam(value = "maxTotalAmount", required = false) BigDecimal maxTotalAmount) {

        return ApiResponse.<PageResponse<OrderResponse>>builder()
                .result(orderService.getAllOrders(page, size, orderCode, status, minTotalAmount, maxTotalAmount))
                .build();
    }


    @GetMapping("{userId}")
    public ApiResponse<PageResponse<OrderResponse>> findOrdersByUserId
            (@PathVariable Long userId,
             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
             @RequestParam(value = "size", required = false, defaultValue = "8") int size)  {
        return ApiResponse.<PageResponse<OrderResponse>>builder()
                .result(orderService.findOrdersByUserId(userId, page,size))
                .build();
    }

    @PostMapping("/{orderId}/confirm")
    public ApiResponse<OrderResponse> confirmOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderService.confirmOrder(orderId);
        return ApiResponse.<OrderResponse>builder()
                .result(orderResponse)
                .message(messageUtils.getAttributeMessage("confirm.success"))
                .build();
    }

    @GetMapping("/detail/{orderId}")
    public ApiResponse<OrderResponse> findOrderById(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderService.findOrderById(orderId);
        return ApiResponse.<OrderResponse>builder()
                .result(orderResponse)
                .build();
    }

    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String newStatus) {
        // Gọi service để cập nhật trạng thái đơn hàng
        Order order = orderService.getOrderById(orderId);
        if(!orderService.isValidOrderStatus(order.getStatus(),newStatus)){
            throw new AppException(ErrorCode.ORDER_STATUS_NOT_VALID);
        }
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
        // Trả về phản hồi thành công với dữ liệu đơn hàng đã cập nhật
        return ApiResponse.<OrderResponse>builder()
                .result(updatedOrder)
                .message(messageUtils.getAttributeMessage("update.success"))
                .build();
    }

    @PutMapping("/{orderId}/client/status")
    public ApiResponse<OrderResponse> clientUpdateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String newStatus,
            @RequestParam Long userId) {
        Order order = orderService.getOrderById(orderId);
        if(!orderService.isValidOrderStatus(order.getStatus(),newStatus)){
            throw new AppException(ErrorCode.ORDER_STATUS_NOT_VALID);
        }
        OrderResponse updatedOrder = orderService.clientUpdateOrderStatus(orderId, newStatus, userId);
        return ApiResponse.<OrderResponse>builder()
                .result(updatedOrder)
                .message(messageUtils.getAttributeMessage("update.success"))
                .build();
    }

}
