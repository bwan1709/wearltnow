package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.product.ProductCreationRequest;
import com.wearltnow.dto.request.product.ProductUpdateRequest;
import com.wearltnow.dto.response.product.ProductResponse;
import com.wearltnow.dto.response.product.ProductSlugCategoryResponse;
import com.wearltnow.service.ProductService;
import com.wearltnow.util.MessageUtils;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;
    MessageUtils messageUtils;

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "8") int size,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "productSize", required = false) String productSize,    // Thêm size
            @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDirection", required = false) String sortDirection) {
        PageResponse<ProductResponse> pageResponse = productService.findAllByDeletedFalse(
                page, size, productName, String.valueOf(categoryId), minPrice, maxPrice, from , to,productSize,color,sortBy,sortDirection);
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(pageResponse)
                .build();
    }

    @GetMapping("/{slug}")
    ApiResponse<List<ProductSlugCategoryResponse>> getProductsByCategorySlug(@PathVariable String slug) {
        return ApiResponse.<List<ProductSlugCategoryResponse>>builder()
                .result(productService.getProductsByCategorySlug(slug))
                .build();
    }
    @GetMapping("/productdetail/{id}")
    public ApiResponse<ProductResponse> getOneProduct(@PathVariable("id") Long id, @ModelAttribute ProductUpdateRequest request) {
        ProductResponse getOneProduct = productService.getOneProduct(id);
        return ApiResponse.<ProductResponse>builder()
                .result(getOneProduct)
                .build();
    }

    @GetMapping("/discounted")
    public ApiResponse<PageResponse<ProductResponse>> getDiscountedProducts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "8") int size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDirection", required = false) String sortDirection) {

        // Gọi service để lấy các sản phẩm có giảm giá
        PageResponse<ProductResponse> discountedProducts = productService.findDiscountedProducts(page, size, sortBy, sortDirection);
        // Trả về kết quả dưới dạng ApiResponse
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(discountedProducts)
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProductResponse> createProduct(@Valid @ModelAttribute ProductCreationRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .message(messageUtils.getAttributeMessage("create.success"))
                .build();
    }

    @PutMapping("/{id}")
   public ApiResponse<ProductResponse> updateProduct(@PathVariable("id") Long id, @Valid @ModelAttribute ProductUpdateRequest request) {
        ProductResponse updateProduct = productService.updateProduct(id, request);
    return ApiResponse.<ProductResponse>builder()
            .result(updateProduct)
            .message(messageUtils.getAttributeMessage("update.success"))
            .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .build();
    }

    @GetMapping("/top-selling-products")
    public ApiResponse<PageResponse<ProductResponse>> getTopSellingProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(defaultValue = "10") int limit) {
        PageResponse<ProductResponse> response = productService.getTopSellingProducts(page, size, limit);
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(response)
                .build();
    }

}
