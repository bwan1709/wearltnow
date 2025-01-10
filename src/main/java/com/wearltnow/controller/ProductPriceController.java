package com.wearltnow.controller;

import com.wearltnow.dto.request.price.ProductPriceRequest;
import com.wearltnow.dto.response.price.ProductPriceResponse;
import com.wearltnow.dto.ApiResponse;
import com.wearltnow.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-prices")
@RequiredArgsConstructor
public class ProductPriceController {

    private final ProductPriceService productPriceService;

    @GetMapping
    public ApiResponse<List<ProductPriceResponse>> getAllProductPrices() {
        List<ProductPriceResponse> response = productPriceService.findAll();
        return ApiResponse.<List<ProductPriceResponse>>builder()
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductPriceResponse> getProductPriceById(@PathVariable Long id) {
        ProductPriceResponse response = productPriceService.getById(id);
        return ApiResponse.<ProductPriceResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/by-price-id/{priceId}")
    public ApiResponse<List<ProductPriceResponse>> getProductPricesByPriceId(@PathVariable Long priceId) {
        List<ProductPriceResponse> response = productPriceService.getAllByPriceId(priceId);
        return ApiResponse.<List<ProductPriceResponse>>builder()
                .result(response)
                .build();
    }

    @PostMapping()
    public ApiResponse<List<ProductPriceResponse>> createProductPrices(@RequestBody List<ProductPriceRequest> productPriceRequests) {
        List<ProductPriceResponse> responses = productPriceService.create(productPriceRequests);
        return ApiResponse.<List<ProductPriceResponse>>builder()
                .result(responses)
                .build();
    }


    @PutMapping("/{id}")
    public ApiResponse<ProductPriceResponse> updateProductPrice(
            @PathVariable Long id,
            @RequestBody ProductPriceRequest productPriceRequest) {
        ProductPriceResponse response = productPriceService.update(id, productPriceRequest);
        return ApiResponse.<ProductPriceResponse>builder()
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProductPrice(@PathVariable Long id) {
        productPriceService.delete(id);
        return ApiResponse.<String>builder()
                .result("Product price deleted successfully")
                .build();
    }
}
