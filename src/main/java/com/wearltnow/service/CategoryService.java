package com.wearltnow.service;

import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.category.CategoryCreationRequest;
import com.wearltnow.dto.response.category.CategoryResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.CategoryMapper;
import com.wearltnow.model.Category;
import com.wearltnow.model.Supplier;
import com.wearltnow.repository.CategoryRepository;
import com.wearltnow.repository.SupplierRepository;
import com.wearltnow.specification.CategorySpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    CloudinaryService cloudinaryService;
    private final SupplierRepository supplierRepository;

    public PageResponse<CategoryResponse> getAllCategories(int page, int size, String categoryName, String supplierName) {
        Sort sort = Sort.by(Sort.Direction.ASC, "categoryId");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Specification<Category> specification = Specification.where(CategorySpecification.isNotDeleted());
        if (categoryName != null) {
            specification = specification.and(CategorySpecification.hasName(categoryName));
        }
        if(supplierName != null){
            specification = specification.and(CategorySpecification.hasSupplier(supplierName));
        }
        var pageData = categoryRepository.findAll(specification,pageable);
        return PageResponse.<CategoryResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(categoryMapper::toCategoryResponse).toList())
                .build();
    }

    public Page<CategoryResponse> getSlugCategories(Pageable pageable,String slug) {
        Page<Category> categoryPage = categoryRepository.findSlug(slug, pageable);
        return categoryPage.map(categoryMapper::toCategoryResponse);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public CategoryResponse createCategory(CategoryCreationRequest request) {
        Category category = categoryMapper.toCategory(request);
        if (request.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
            category.setParentCategory(parentCategory);
        }
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                        .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        category.setSupplier(supplier);

        String imageUrl = cloudinaryService.uploadImage(request.getImage());
        category.setImage(imageUrl);

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public CategoryResponse updateCategory(Long categoryId, CategoryCreationRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        categoryMapper.updateCategory(request, category);

        if (request.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        category.setSupplier(supplier);
        String imageUrl = cloudinaryService.uploadImage(request.getImage());
        category.setImage(imageUrl);

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void delete(Long categoryId){
        categoryRepository.deleteById(categoryId);
    }

}
