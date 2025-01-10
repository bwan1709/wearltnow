package com.wearltnow.mapper;

import com.wearltnow.dto.response.product.ProductFavoriteResponse;
import com.wearltnow.model.ProductFavorite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductFavoriteMapper {
    ProductFavoriteResponse toResponse(ProductFavorite productComment);
}
