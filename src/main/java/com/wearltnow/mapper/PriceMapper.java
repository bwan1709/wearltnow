package com.wearltnow.mapper;

import com.wearltnow.dto.request.price.PriceRequest;
import com.wearltnow.dto.response.price.PriceResponse;
import com.wearltnow.model.Price;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceMapper {
    Price toEntity(PriceRequest priceRequestDto);

    PriceResponse toResponse(Price price);
}
