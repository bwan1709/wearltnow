package com.wearltnow.mapper;

import com.wearltnow.dto.request.product.ProductCreationRequest;
import com.wearltnow.dto.request.product.ProductUpdateRequest;
import com.wearltnow.dto.response.product.ProductResponse;
import com.wearltnow.dto.response.product.ProductSlugCategoryResponse;
import com.wearltnow.model.Product;
import com.wearltnow.service.ProductService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Autowired
    ProductService productService = null;
    @Mapping(target = "image", ignore = true) // Bỏ qua ảnh chính
    Product toProduct(ProductCreationRequest request);

    @Mappings({
            @Mapping(target = "images", source = "productImages"),
            @Mapping(target = "inventories", source = "productInventories"),
            @Mapping(target = "category", source = "category"),
            @Mapping(target = "price", ignore = true)
    })
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "category", source = "category")
    @Mapping(target = "inventories", source = "productInventories")
    @Mapping(target = "price", ignore = true)
    ProductSlugCategoryResponse toProductSlugCategoryResponse(Product product);
    // Phương thức cập nhật sản phẩm
    @Mapping(target = "image", ignore = true) // Bỏ qua ánh xạ ảnh chính
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);
}
