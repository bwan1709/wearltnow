package com.wearltnow.controller;

import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.category.CategoryCreationRequest;
import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.category.CategoryResponse;
import com.wearltnow.service.CategoryService;
import com.wearltnow.util.MessageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    MessageUtils messageUtils;
    CategoryService categoryService;

    @GetMapping
    public ApiResponse<PageResponse<CategoryResponse>> getAllCategories(
            @RequestParam(value = "page",required = false,defaultValue = "1") int page, // Default page is 0
            @RequestParam(value = "size", required = false,defaultValue = "6") int size,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "supplierName", required = false) String supplierName) { // Default size is 4

        return ApiResponse.<PageResponse<CategoryResponse>>builder()
                .result(categoryService.getAllCategories(page,size,name,supplierName))
                .build();
    }

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryCreationRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .message(messageUtils.getAttributeMessage("create.success"))
                .result(categoryService.createCategory(request))
                .build();
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long categoryId,
            @ModelAttribute CategoryCreationRequest request) {
        CategoryResponse categoryResponse = categoryService.updateCategory(categoryId, request);
        return ApiResponse.<CategoryResponse>builder()
                .message(messageUtils.getAttributeMessage("update.success"))
                .result(categoryResponse)
                .build();
    }

    @GetMapping("/{slug}")
    public ApiResponse<List<CategoryResponse>> getSlugCategories(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page, // Default page is 0
            @RequestParam(defaultValue = "8") int size) { // Default size is 4
        Pageable pageable = PageRequest.of(page, size);

        // Lấy Page<CategoryResponse> từ service
        Page<CategoryResponse> categoryPage = categoryService.getSlugCategories(pageable,slug);

        // Chuyển đổi Page<CategoryResponse> sang List<CategoryResponse>
        List<CategoryResponse> categories = categoryPage.getContent(); // Lấy danh sách nội dung

        // Tạo ApiResponse với thông tin chi tiết
        return ApiResponse.<List<CategoryResponse>>builder()
                .code(1000) // Thêm mã phản hồi
                .result(categories)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("delete.success"))
                .build();
    }
}
