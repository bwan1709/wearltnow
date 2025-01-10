package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.product.ProductFavoriteResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.Product;
import com.wearltnow.model.User;
import com.wearltnow.repository.ProductRepository;
import com.wearltnow.repository.UserRepository;
import com.wearltnow.service.ProductFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class ProductFavoriteController {

    private final ProductFavoriteService productFavoriteService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // Thêm sản phẩm vào danh sách yêu thích
    @PostMapping("/add")
    public ApiResponse<Void> addFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        productFavoriteService.addFavorite(user, product);
        return ApiResponse.<Void>builder()
                .message("Added favorite")
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<ProductFavoriteResponse>> getFavoritesByUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        List<ProductFavoriteResponse> favorites = productFavoriteService.getFavoritesByUser(user);
        return ApiResponse.<List<ProductFavoriteResponse>>builder()
                .result(favorites)
                .build();
    }

    // Xóa sản phẩm khỏi danh sách yêu thích
    @DeleteMapping("/remove")
    public ApiResponse<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        productFavoriteService.removeFavorite(user, product);
        return ApiResponse.<Void>builder()
                    .message("Removed favorite")
                    .build();
    }

    // Kiểm tra xem sản phẩm có trong danh sách yêu thích hay không
    @GetMapping("/is-favorite")
    public ApiResponse<Boolean> isFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        boolean isFavorite = productFavoriteService.isFavorite(user, product);
        return ApiResponse.<Boolean>builder()
                .result(isFavorite)
                .build();
    }
}
