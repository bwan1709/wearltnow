package com.wearltnow.controller;

import com.wearltnow.dto.request.price.PriceRequest;
import com.wearltnow.dto.response.price.PriceResponse;
import com.wearltnow.service.PriceService;
import com.wearltnow.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping
    public ApiResponse<List<PriceResponse>> getAllPrices() {
        List<PriceResponse> response = priceService.findAll();
        return ApiResponse.<List<PriceResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PriceResponse> getPriceById(@PathVariable Long id) {
        PriceResponse response = priceService.getById(id);
        return ApiResponse.<PriceResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping
    public ApiResponse<PriceResponse> createPrice(@RequestBody PriceRequest priceRequest) {
        PriceResponse response = priceService.create(priceRequest);
        return ApiResponse.<PriceResponse>builder()
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<PriceResponse> updatePrice(@PathVariable Long id, @RequestBody PriceRequest priceRequest) {
        PriceResponse response = priceService.update(id, priceRequest);
        return ApiResponse.<PriceResponse>builder()
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePrice(@PathVariable Long id) {
        priceService.delete(id);
        return ApiResponse.<String>builder()
                .result("Price deleted successfully")
                .build();
    }
}
