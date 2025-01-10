package com.wearltnow.mapper;

import com.wearltnow.dto.request.product.ProductInventoryCreationRequest;
import com.wearltnow.dto.request.product.ProductInventoryUpdationRequest;
import com.wearltnow.dto.response.product.ProductInventoryResponse;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductInventory;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProductInventoryMapperImpl implements ProductInventoryMapper {

    @Override
    public ProductInventory toProductInventory(ProductInventoryCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        ProductInventory.ProductInventoryBuilder productInventory = ProductInventory.builder();

        productInventory.color( request.getColor() );
        productInventory.size( request.getSize() );
        productInventory.quantity( request.getQuantity() );
        productInventory.purchasePrice( request.getPurchasePrice() );

        return productInventory.build();
    }

    @Override
    public ProductInventoryResponse toProductInventoryResponse(ProductInventory request) {
        if ( request == null ) {
            return null;
        }

        ProductInventoryResponse.ProductInventoryResponseBuilder productInventoryResponse = ProductInventoryResponse.builder();

        productInventoryResponse.productId( requestProductProductId( request ) );
        productInventoryResponse.productInventoryId( request.getProductInventoryId() );
        productInventoryResponse.color( request.getColor() );
        productInventoryResponse.size( request.getSize() );
        productInventoryResponse.quantity( request.getQuantity() );
        productInventoryResponse.purchasePrice( request.getPurchasePrice() );

        productInventoryResponse.totalPrice( calculateTotalPrice(request.getQuantity(), request.getPurchasePrice()) );

        return productInventoryResponse.build();
    }

    @Override
    public void updateProductInventory(ProductInventory productInventory, ProductInventoryUpdationRequest request) {
        if ( request == null ) {
            return;
        }

        productInventory.setColor( request.getColor() );
        productInventory.setSize( request.getSize() );
        productInventory.setQuantity( request.getQuantity() );
        productInventory.setPurchasePrice( request.getPurchasePrice() );
    }

    private Long requestProductProductId(ProductInventory productInventory) {
        if ( productInventory == null ) {
            return null;
        }
        Product product = productInventory.getProduct();
        if ( product == null ) {
            return null;
        }
        Long productId = product.getProductId();
        if ( productId == null ) {
            return null;
        }
        return productId;
    }
}
