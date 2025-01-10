package com.wearltnow.mapper;

import com.wearltnow.dto.request.price.ProductPriceRequest;
import com.wearltnow.dto.response.price.ProductPriceResponse;
import com.wearltnow.model.Price;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductPrice;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProductPriceMapperImpl implements ProductPriceMapper {

    @Override
    public ProductPriceResponse toResponse(ProductPrice productPrice) {
        if ( productPrice == null ) {
            return null;
        }

        ProductPriceResponse.ProductPriceResponseBuilder productPriceResponse = ProductPriceResponse.builder();

        productPriceResponse.productId( productPriceProductProductId( productPrice ) );
        productPriceResponse.productPriceId( productPricePriceId( productPrice ) );
        productPriceResponse.name( productPricePriceName( productPrice ) );
        productPriceResponse.id( productPrice.getId() );
        productPriceResponse.priceValue( productPrice.getPriceValue() );

        return productPriceResponse.build();
    }

    @Override
    public ProductPrice toEntity(ProductPriceRequest productPriceDto) {
        if ( productPriceDto == null ) {
            return null;
        }

        ProductPrice.ProductPriceBuilder productPrice = ProductPrice.builder();

        productPrice.product( productPriceRequestToProduct( productPriceDto ) );
        productPrice.price( productPriceRequestToPrice( productPriceDto ) );
        productPrice.priceValue( productPriceDto.getPriceValue() );

        return productPrice.build();
    }

    private Long productPriceProductProductId(ProductPrice productPrice) {
        if ( productPrice == null ) {
            return null;
        }
        Product product = productPrice.getProduct();
        if ( product == null ) {
            return null;
        }
        Long productId = product.getProductId();
        if ( productId == null ) {
            return null;
        }
        return productId;
    }

    private Long productPricePriceId(ProductPrice productPrice) {
        if ( productPrice == null ) {
            return null;
        }
        Price price = productPrice.getPrice();
        if ( price == null ) {
            return null;
        }
        Long id = price.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productPricePriceName(ProductPrice productPrice) {
        if ( productPrice == null ) {
            return null;
        }
        Price price = productPrice.getPrice();
        if ( price == null ) {
            return null;
        }
        String name = price.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    protected Product productPriceRequestToProduct(ProductPriceRequest productPriceRequest) {
        if ( productPriceRequest == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.productId( productPriceRequest.getProductId() );

        return product.build();
    }

    protected Price productPriceRequestToPrice(ProductPriceRequest productPriceRequest) {
        if ( productPriceRequest == null ) {
            return null;
        }

        Price.PriceBuilder price = Price.builder();

        price.id( productPriceRequest.getProductPriceId() );

        return price.build();
    }
}
