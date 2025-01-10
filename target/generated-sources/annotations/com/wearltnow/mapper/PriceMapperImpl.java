package com.wearltnow.mapper;

import com.wearltnow.dto.request.price.PriceRequest;
import com.wearltnow.dto.response.price.PriceResponse;
import com.wearltnow.model.Price;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:16+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class PriceMapperImpl implements PriceMapper {

    @Override
    public Price toEntity(PriceRequest priceRequestDto) {
        if ( priceRequestDto == null ) {
            return null;
        }

        Price.PriceBuilder price = Price.builder();

        price.code( priceRequestDto.getCode() );
        price.name( priceRequestDto.getName() );
        price.startDate( priceRequestDto.getStartDate() );
        price.endDate( priceRequestDto.getEndDate() );

        return price.build();
    }

    @Override
    public PriceResponse toResponse(Price price) {
        if ( price == null ) {
            return null;
        }

        PriceResponse.PriceResponseBuilder priceResponse = PriceResponse.builder();

        priceResponse.id( price.getId() );
        priceResponse.code( price.getCode() );
        priceResponse.name( price.getName() );
        priceResponse.startDate( price.getStartDate() );
        priceResponse.endDate( price.getEndDate() );

        return priceResponse.build();
    }
}
