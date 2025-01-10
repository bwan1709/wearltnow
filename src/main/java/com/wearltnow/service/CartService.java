package com.wearltnow.service;

import com.wearltnow.dto.response.cart.CartItemResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.model.*;
import com.wearltnow.repository.CartRepository;
import com.wearltnow.repository.ProductInventoryRepository;
import com.wearltnow.repository.ProductRepository;
import com.wearltnow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductInventoryService productInventoryService;
    @Autowired
    private ProductInventoryRepository productInventoryRepository;
    @Autowired
    private ProductService productService;

    public void addToCart(Long userId, List<CartItem> cartItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElse(new Cart());
        cart.setUser(user);

        for (CartItem itemRequest : cartItems) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

            // Check inventory for the product
            ProductInventory productInventory = productInventoryRepository.findByProductAndColorAndSize(
                            product, itemRequest.getColor(), itemRequest.getSize())
                    .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOTFOUND));

            if (productInventory.getQuantity() < itemRequest.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_INVENTORY);
            }

            // Kiểm tra xem sản phẩm đã có trong giỏ chưa
            boolean itemExists = cart.getItems().stream()
                    .anyMatch(existingItem -> existingItem.getProductId().equals(itemRequest.getProductId())
                            && existingItem.getSize().equals(itemRequest.getSize())
                            && existingItem.getColor().equals(itemRequest.getColor()));

            if (itemExists) {
                // Nếu đã tồn tại, cập nhật số lượng
                cart.getItems().stream()
                        .filter(existingItem -> existingItem.getProductId().equals(itemRequest.getProductId())
                                && existingItem.getSize().equals(itemRequest.getSize())
                                && existingItem.getColor().equals(itemRequest.getColor()))
                        .findFirst()
                        .ifPresent(existingItem -> existingItem.setQuantity(existingItem.getQuantity() + itemRequest.getQuantity()));
            } else {
                // Nếu chưa tồn tại, thêm mới
                cart.addItem(itemRequest.getProductId(), itemRequest.getQuantity(), itemRequest.getSize(), itemRequest.getColor());
            }
        }

        cartRepository.save(cart);
    }


    public void removeCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOTFOUND));

        // Xóa giỏ hàng cùng với tất cả các item trong nó
        cartRepository.delete(cart);
    }

    public void removeCartItem(Long userId, Long cartItemId) {
        // Tìm người dùng theo userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        // Tìm giỏ hàng của người dùng
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOTFOUND));

        // Tìm item trong giỏ hàng theo cartItemId
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOTFOUND));

        // Xóa item khỏi giỏ hàng
        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
    }

    public List<CartItemResponse> getCartItemsResonse(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOTFOUND));

        return cart.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

                    // Lấy giá từ phương thức getProductPrice để kiểm tra giá từ bảng ProductPrice hoặc giá mặc định từ Product
                    Double price = productService.getProductPrice(item.getProductId());

                    return new CartItemResponse(
                            item.getCartItemId(),
                            item.getProductId(),
                            item.getQuantity(),
                            item.getSize(),
                            item.getColor(),
                            product.getName(),
                            price, // Trả về giá lấy từ getProductPrice
                            product.getImage()
                    );
                })
                .collect(Collectors.toList());
    }



    public List<CartItem> getCartItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOTFOUND));

        // Chuyển đổi các item trong cart thành CartItemResponse
        return cart.getItems().stream()
                .map(item -> new CartItem(
                        item.getCartItemId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getSize(),
                        item.getColor()))
                .collect(Collectors.toList());
    }

    public void clearAllCart(Long userId) {
        // Tìm người dùng theo userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));

        // Tìm giỏ hàng của người dùng
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOTFOUND));

        // Xóa tất cả các item trong giỏ hàng
        cart.getItems().clear();

        // Lưu giỏ hàng sau khi xóa các item
        cartRepository.save(cart);
    }

}
