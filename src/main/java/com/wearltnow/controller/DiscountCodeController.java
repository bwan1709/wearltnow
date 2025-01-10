package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.discount.DiscountCodeRequest;
import com.wearltnow.dto.response.discount.DiscountCodeResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.DiscountCode;
import com.wearltnow.model.Order;
import com.wearltnow.model.User;
import com.wearltnow.repository.UserRepository;
import com.wearltnow.service.DiscountCodeService;
import com.wearltnow.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/discount-codes")
public class DiscountCodeController {

    @Autowired
    private DiscountCodeService discountCodeService;

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ApiResponse<PageResponse<DiscountCodeResponse>> getAllDiscounts
            (@RequestParam(value = "page", required = false, defaultValue = "1") int page,
             @RequestParam(value = "size", required = false, defaultValue = "8") int size) {
        return ApiResponse.<PageResponse<DiscountCodeResponse>>builder()
                .result(discountCodeService.getAllDiscount(page, size))
                .build();
    }
    // API kiểm tra và áp dụng mã giảm giá cho đơn hàng
    @PostMapping("/apply")
    public BigDecimal applyDiscountCode(@RequestParam String code, @RequestParam Long orderId, @RequestParam Long userId) {
        Optional<DiscountCode> discountCodeOpt = discountCodeService.findDiscountCodeByCode(code);
        if (!discountCodeOpt.isPresent()) {
            throw new AppException(ErrorCode.DISCOUNT_CODE_NOT_VALID);
        }

        DiscountCode discountCode = discountCodeOpt.get();
        User user = orderService.getUserById(userId);

        // Kiểm tra xem mã giảm giá có hợp lệ cho người dùng không
        if (!discountCodeService.isDiscountValid(discountCode, user)) {
            throw new AppException(ErrorCode.DISCOUNT_CODE_NOT_VALID_FOR_USER);
        }

        Order order = orderService.getOrderById(orderId);

        // Áp dụng mã giảm giá vào đơn hàng
        BigDecimal discountedAmount = discountCodeService.applyDiscount(discountCode, order.getOrderTotal());

        // Lưu số tiền giảm vào order
        order.setDiscountAmount(discountedAmount);

        // Cập nhật giá trị finalAmount (giá trị cuối cùng sau khi giảm giá)
        order.setOrderAmount(order.getOrderTotal().subtract(discountedAmount));

        // Lưu đơn hàng đã cập nhật
        orderService.updateOrder(order);

        return discountedAmount;  // Trả về số tiền giảm
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<DiscountCodeResponse>> getDiscountCodesForUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        // Lấy danh sách mã giảm giá hợp lệ cho người dùng
        List<DiscountCodeResponse> discountCodes = discountCodeService.findDiscountCodesForUser(user);

        return ApiResponse.<List<DiscountCodeResponse>>builder()
                .result(discountCodes)
                .message("Successfully fetched discount codes for user.")
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<DiscountCodeResponse> createDiscountCode(@RequestBody DiscountCodeRequest discountCodeRequest) {

        DiscountCodeResponse  discountCodeResponse = discountCodeService.createDiscountCode(discountCodeRequest);
        // Trả về response với thông điệp
        return ApiResponse.<DiscountCodeResponse>builder()
                .result(discountCodeResponse)
                .message("Discount code created successfully.")
                .build();
    }

        @PostMapping("/{discountCodeId}/users")
        public ApiResponse<String> addUserToDiscountCode(
                @PathVariable Long discountCodeId,
                @RequestBody List<Long> userIds) {
            try {
                discountCodeService.addUsersToDiscountCode(discountCodeId, userIds);
                return ApiResponse.<String>builder()
                        .message("Áp dụng mã giảm giá thành công!!")
                        .build();
            } catch (Exception e) {
                return ApiResponse.<String>builder()
                        .message(e.getMessage())
                        .build();
            }
        }

        @DeleteMapping("{id}")
    public ApiResponse<Void> deleteDiscountCode(@PathVariable Long id) {
        discountCodeService.delete(id);
        return ApiResponse.<Void>builder()
                .message("delete discount code successfully.")
                .build();
        }

}
