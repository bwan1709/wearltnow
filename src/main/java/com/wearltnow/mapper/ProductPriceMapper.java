package com.wearltnow.mapper;

import com.wearltnow.dto.request.price.ProductPriceRequest;
import com.wearltnow.dto.response.price.ProductPriceResponse;
import com.wearltnow.model.ProductPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductPriceMapper {
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "price.id", target = "productPriceId")
    @Mapping(source = "price.name", target = "name")
    ProductPriceResponse toResponse(ProductPrice productPrice);

    @Mapping(source = "productId", target = "product.productId")
    @Mapping(source = "productPriceId", target = "price.id")
    ProductPrice toEntity(ProductPriceRequest productPriceDto);
}
