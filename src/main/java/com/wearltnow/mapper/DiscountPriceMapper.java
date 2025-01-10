package com.wearltnow.mapper;

import com.wearltnow.dto.request.discount_price.DiscountPriceRequest;
import com.wearltnow.dto.response.discount_price.DiscountPriceResponse;
import com.wearltnow.model.DiscountPrice;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DiscountPriceMapper {

    @Mapping(source = "productId", target = "product.productId")
    DiscountPrice toEntity(DiscountPriceRequest request);

    void updateDiscountPrice(@MappingTarget DiscountPrice discountPrice, DiscountPriceRequest request);

    // Cập nhật phương thức toResponse để ánh xạ đầy đủ các trường của product
    @Mapping(source = "product.name", target = "product.name")
    @Mapping(source = "product.price", target = "product.price")
    @Mapping(source = "product.description", target = "product.description")
    @Mapping(source = "product.category", target = "product.category")
    @Mapping(source = "product.image", target = "product.image")
    @Mapping(source = "product.productInventories", target = "product.inventories")
    @Mapping(source = "product.productId", target = "productId")
    DiscountPriceResponse toResponse(DiscountPrice discountPrice);
}

