package com.wearltnow.service;


import com.wearltnow.dto.request.product.ProductInventoryCreationRequest;
import com.wearltnow.dto.request.product.ProductInventoryUpdationRequest;
import com.wearltnow.dto.response.product.ProductInventoryResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.mapper.ProductInventoryMapper;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductInventory;
import com.wearltnow.repository.ProductInventoryRepository;
import com.wearltnow.repository.ProductRepository;
import com.wearltnow.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductInventoryService {

    ProductInventoryRepository productInventoryRepository;
    ProductRepository productRepository;
    ProductInventoryMapper productInventoryMapper;
    ProductService productService;

    // Thêm tồn kho mới cho sản phẩm
    @Transactional
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public ProductInventoryResponse addInventory(ProductInventoryCreationRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        // Kiểm tra nếu đã tồn tại ProductInventory với cùng product, color và size
        Optional<ProductInventory> existingInventoryOpt = productInventoryRepository
                .findByProductAndColorAndSize(product, request.getColor(), request.getSize());

        ProductInventory productInventory;

        if (existingInventoryOpt.isPresent()) {
            // Nếu tồn tại, cập nhật số lượng
            productInventory = existingInventoryOpt.get();
            productInventory.setQuantity(productInventory.getQuantity() + request.getQuantity());
        } else {
            // Nếu không tồn tại, tạo mới
            productInventory = productInventoryMapper.toProductInventory(request);
            productInventory.setProduct(product);
        }

        ProductInventory savedInventory = productInventoryRepository.save(productInventory);
        productService.deleteAllProductCache();
        return productInventoryMapper.toProductInventoryResponse(savedInventory);
    }

    // Cập nhật thông tin tồn kho
    @Transactional
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public ProductInventoryResponse updateInventory(Long inventoryId, ProductInventoryUpdationRequest request) {
        ProductInventory productInventory = productInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOTFOUND));

        productInventoryMapper.updateProductInventory(productInventory, request);
        productService.deleteAllProductCache();
        return productInventoryMapper.toProductInventoryResponse(productInventoryRepository.save(productInventory));
    }

    // Xóa tồn kho
    @Transactional
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void deleteInventory(Long inventoryId) {
        ProductInventory productInventory = productInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOTFOUND));
        productService.deleteAllProductCache();
        productInventoryRepository.delete(productInventory);
    }

    // Lấy danh sách tồn kho theo sản phẩm
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<ProductInventoryResponse> getInventoriesByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        List<ProductInventory> inventories = productInventoryRepository.findByProduct(product);
        return inventories.stream()
                .map(productInventoryMapper::toProductInventoryResponse)
                .collect(Collectors.toList());
    }

    // Lấy tồn kho theo ID
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public ProductInventoryResponse getInventoryById(Long inventoryId) {
        ProductInventory productInventory = productInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOTFOUND));
        return productInventoryMapper.toProductInventoryResponse(productInventory);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<ProductInventoryResponse> getAllInventories() {
        List<ProductInventory> productInventoryList = productInventoryRepository.findAll();
        return productInventoryList.stream()
                .map(productInventoryMapper::toProductInventoryResponse)
                .toList();
    }

}
