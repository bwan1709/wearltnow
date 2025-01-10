package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.discount_price.DiscountPriceRequest;
import com.wearltnow.dto.response.discount_price.DiscountPriceResponse;
import com.wearltnow.service.DiscountPriceService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discount-price")
@RequiredArgsConstructor
public class DiscountPriceController {

    private final DiscountPriceService discountPriceService;

    // Lấy tất cả DiscountPrice
    @GetMapping
    public ApiResponse<PageResponse<DiscountPriceResponse>> getAllDiscountPrices(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size) {
        return ApiResponse.<PageResponse<DiscountPriceResponse>>builder()
                .result(discountPriceService.getAllDiscountPrices(page, size))
                .build();
    }

    // Lấy DiscountPrice theo ID
    @GetMapping("/{id}")
    public ApiResponse<DiscountPriceResponse> getDiscountPriceById(@PathVariable Long id) {
        return ApiResponse.<DiscountPriceResponse>builder()
                .result(discountPriceService.getDiscountPriceById(id))
                .build();
    }

    // Tạo mới DiscountPrice
    @PostMapping
    public ApiResponse<DiscountPriceResponse> createDiscountPrice(@RequestBody DiscountPriceRequest request) throws Exception {
        return ApiResponse.<DiscountPriceResponse>builder()
                .result(discountPriceService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DiscountPriceResponse> updateDiscountPrice(
            @PathVariable Long id, @RequestBody DiscountPriceRequest request) throws Exception {
        return ApiResponse.<DiscountPriceResponse>builder()
                .result(discountPriceService.update(id, request))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> deleteDiscountPrice(@PathVariable Long id) {
        discountPriceService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Delete discount price successfully.")
                .build();
    }
}
