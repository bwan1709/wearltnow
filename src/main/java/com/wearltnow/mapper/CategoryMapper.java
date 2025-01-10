package com.wearltnow.mapper;

import com.wearltnow.dto.request.category.CategoryCreationRequest;
import com.wearltnow.dto.response.category.CategoryResponse;
import com.wearltnow.dto.response.category.SubCategoryResponse;
import com.wearltnow.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "image", ignore = true)
    Category toCategory(CategoryCreationRequest request);

    @Mapping(source = "parentCategory.categoryId", target = "parentId")
    @Mapping(target = "subCategories", expression = "java(mapSubCategories(category.getSubCategories()))")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(source = "parentCategory.categoryId", target = "parentId")
    SubCategoryResponse toSubCategoryResponse(Category category);

    @Mapping(target = "image", ignore = true) // Bỏ qua trường hình ảnh trong cập nhật
    void updateCategory(CategoryCreationRequest request, @MappingTarget Category category);

    // Sửa đổi để nhận vào List<Category> thay vì List<CategoryResponse>
    default List<SubCategoryResponse> mapSubCategories(List<Category> subCategories) {
        if (subCategories == null) {
            return Collections.emptyList();
        }
        return subCategories.stream()
                .filter(subCategory -> subCategory.getDeleted() == null || !subCategory.getDeleted()) // Lọc chưa bị xóa
                .map(this::toSubCategoryResponse) // Sử dụng toSubCategoryResponse để ánh xạ
                .collect(Collectors.toList());
    }
}
