package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.request.product.ProductCommentRequest;
import com.wearltnow.dto.response.product.ProductCommentResponse;
import com.wearltnow.service.ProductCommentService;
import com.wearltnow.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class ProductCommentController {

    private final ProductCommentService productCommentService;
    private final UserService userService;

    public ProductCommentController(ProductCommentService productCommentService, UserService userService) {
        this.productCommentService = productCommentService;
        this.userService = userService;
    }

    // Tạo bình luận mới
    @PostMapping()
    public ResponseEntity<ApiResponse<ProductCommentResponse>> createComment(@RequestBody ProductCommentRequest request) {
        // Tạo và lưu bình luận
        ProductCommentResponse response = productCommentService.createComment(request);

        // Trả về ApiResponse với thông điệp và kết quả
        ApiResponse<ProductCommentResponse> apiResponse = ApiResponse.<ProductCommentResponse>builder()
                .message("Comment đã được tạo thành công")
                .result(response)
                .build();

        // Trả về phản hồi HTTP với mã trạng thái CREATED (201)
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // Lấy thông tin bình luận theo ID
    @GetMapping("/{id}")
    public ApiResponse<ProductCommentResponse> getCommentById(@PathVariable Long id) {
        ProductCommentResponse response = productCommentService.getCommentById(id);
        if (response == null) {
            return ApiResponse.<ProductCommentResponse>builder()
                    .message("Comment không tìm thấy")
                    .result(null)
                    .build();
        }
        return ApiResponse.<ProductCommentResponse>builder()
                .message("Lấy comment thành công")
                .result(response)
                .build();
    }

    // Lấy danh sách tất cả bình luận của sản phẩm theo ID
    @GetMapping("/product/{productId}")
    public ApiResponse<ProductCommentResponse> getAllCommentsByProductId(@PathVariable Long productId) {
        ProductCommentResponse response = productCommentService.getAllCommentsAndAverageRateByProductId(productId);
        return ApiResponse.<ProductCommentResponse>builder()
                .message("Danh sách comment và trung bình rate đã được lấy")
                .result(response)
                .build();
    }

}
