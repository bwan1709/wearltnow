package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.cart.CartItemResponse;
import com.wearltnow.model.CartItem;
import com.wearltnow.service.CartService;
import com.wearltnow.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v0/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MessageUtils messageUtils;

    @PostMapping("/save")
    public ApiResponse<Void> saveCart(
            @RequestParam Long userId,
            @RequestBody List<CartItem> cartItems) {
            cartService.addToCart(userId, cartItems);
            return ApiResponse.<Void>builder()
                    .message(messageUtils.getAttributeMessage("add-to-cart.success"))
                    .build();
    }

    @GetMapping("/get")
    public ApiResponse<List<CartItemResponse>> getCart(@RequestParam Long userId) {
        List<CartItemResponse> cartItems = cartService.getCartItemsResonse(userId);
        return ApiResponse.<List<CartItemResponse>>builder()
                .result(cartItems)
                .build();
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ApiResponse<Void> removeCartItem(@RequestParam Long userId, @PathVariable Long cartItemId) {
        cartService.removeCartItem(userId, cartItemId);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("remove-cart-item.success"))
                .build();
    }

    @DeleteMapping("/clear")
    public ApiResponse<Void> clearAllCart(@RequestParam Long userId) {
        cartService.clearAllCart(userId);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("clear-cart.success"))
                .build();
    }

}
