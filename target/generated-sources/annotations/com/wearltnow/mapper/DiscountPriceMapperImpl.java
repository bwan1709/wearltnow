package com.wearltnow.mapper;

import com.wearltnow.dto.request.discount_price.DiscountPriceRequest;
import com.wearltnow.dto.response.category.CategoryResponse;
import com.wearltnow.dto.response.category.SubCategoryResponse;
import com.wearltnow.dto.response.discount_price.DiscountPriceResponse;
import com.wearltnow.dto.response.product.ProductInventoryResponse;
import com.wearltnow.dto.response.product.ProductResponse;
import com.wearltnow.dto.response.supplier.SupplierResponse;
import com.wearltnow.model.Category;
import com.wearltnow.model.DiscountPrice;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductInventory;
import com.wearltnow.model.Supplier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class DiscountPriceMapperImpl implements DiscountPriceMapper {

    @Override
    public DiscountPrice toEntity(DiscountPriceRequest request) {
        if ( request == null ) {
            return null;
        }

        DiscountPrice.DiscountPriceBuilder discountPrice = DiscountPrice.builder();

        discountPrice.product( discountPriceRequestToProduct( request ) );
        discountPrice.discountRate( request.getDiscountRate() );
        discountPrice.startDate( request.getStartDate() );
        discountPrice.endDate( request.getEndDate() );

        return discountPrice.build();
    }

    @Override
    public void updateDiscountPrice(DiscountPrice discountPrice, DiscountPriceRequest request) {
        if ( request == null ) {
            return;
        }

        discountPrice.setDiscountRate( request.getDiscountRate() );
        discountPrice.setStartDate( request.getStartDate() );
        discountPrice.setEndDate( request.getEndDate() );
    }

    @Override
    public DiscountPriceResponse toResponse(DiscountPrice discountPrice) {
        if ( discountPrice == null ) {
            return null;
        }

        DiscountPriceResponse discountPriceResponse = new DiscountPriceResponse();

        discountPriceResponse.setProduct( productToProductResponse( discountPrice.getProduct() ) );
        discountPriceResponse.setProductId( discountPriceProductProductId( discountPrice ) );
        discountPriceResponse.setId( discountPrice.getId() );
        discountPriceResponse.setDiscountRate( discountPrice.getDiscountRate() );
        discountPriceResponse.setStartDate( discountPrice.getStartDate() );
        discountPriceResponse.setEndDate( discountPrice.getEndDate() );

        return discountPriceResponse;
    }

    protected Product discountPriceRequestToProduct(DiscountPriceRequest discountPriceRequest) {
        if ( discountPriceRequest == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.productId( discountPriceRequest.getProductId() );

        return product.build();
    }

    protected List<SubCategoryResponse> categoryListToSubCategoryResponseList(List<Category> list) {
        if ( list == null ) {
            return null;
        }

        List<SubCategoryResponse> list1 = new ArrayList<SubCategoryResponse>( list.size() );
        for ( Category category : list ) {
            list1.add( categoryToSubCategoryResponse( category ) );
        }

        return list1;
    }

    protected SubCategoryResponse categoryToSubCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        SubCategoryResponse.SubCategoryResponseBuilder subCategoryResponse = SubCategoryResponse.builder();

        subCategoryResponse.categoryId( category.getCategoryId() );
        subCategoryResponse.name( category.getName() );
        subCategoryResponse.slug( category.getSlug() );
        subCategoryResponse.subCategories( categoryListToSubCategoryResponseList( category.getSubCategories() ) );

        return subCategoryResponse.build();
    }

    protected SupplierResponse supplierToSupplierResponse(Supplier supplier) {
        if ( supplier == null ) {
            return null;
        }

        SupplierResponse.SupplierResponseBuilder supplierResponse = SupplierResponse.builder();

        supplierResponse.supplierId( supplier.getSupplierId() );
        supplierResponse.description( supplier.getDescription() );
        supplierResponse.email( supplier.getEmail() );
        supplierResponse.contactPerson( supplier.getContactPerson() );
        supplierResponse.name( supplier.getName() );
        supplierResponse.website( supplier.getWebsite() );
        supplierResponse.phone( supplier.getPhone() );
        supplierResponse.taxCode( supplier.getTaxCode() );

        return supplierResponse.build();
    }

    protected CategoryResponse categoryToCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.categoryId( category.getCategoryId() );
        categoryResponse.name( category.getName() );
        categoryResponse.image( category.getImage() );
        categoryResponse.description( category.getDescription() );
        categoryResponse.status( category.getStatus() );
        categoryResponse.slug( category.getSlug() );
        categoryResponse.subCategories( categoryListToSubCategoryResponseList( category.getSubCategories() ) );
        categoryResponse.supplier( supplierToSupplierResponse( category.getSupplier() ) );

        return categoryResponse.build();
    }

    protected ProductInventoryResponse productInventoryToProductInventoryResponse(ProductInventory productInventory) {
        if ( productInventory == null ) {
            return null;
        }

        ProductInventoryResponse.ProductInventoryResponseBuilder productInventoryResponse = ProductInventoryResponse.builder();

        productInventoryResponse.productInventoryId( productInventory.getProductInventoryId() );
        productInventoryResponse.color( productInventory.getColor() );
        productInventoryResponse.size( productInventory.getSize() );
        productInventoryResponse.quantity( productInventory.getQuantity() );
        productInventoryResponse.purchasePrice( productInventory.getPurchasePrice() );

        return productInventoryResponse.build();
    }

    protected List<ProductInventoryResponse> productInventoryListToProductInventoryResponseList(List<ProductInventory> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductInventoryResponse> list1 = new ArrayList<ProductInventoryResponse>( list.size() );
        for ( ProductInventory productInventory : list ) {
            list1.add( productInventoryToProductInventoryResponse( productInventory ) );
        }

        return list1;
    }

    protected ProductResponse productToProductResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.name( product.getName() );
        productResponse.price( product.getPrice() );
        productResponse.description( product.getDescription() );
        productResponse.category( categoryToCategoryResponse( product.getCategory() ) );
        productResponse.image( product.getImage() );
        productResponse.inventories( productInventoryListToProductInventoryResponseList( product.getProductInventories() ) );
        productResponse.productId( product.getProductId() );

        return productResponse.build();
    }

    private Long discountPriceProductProductId(DiscountPrice discountPrice) {
        if ( discountPrice == null ) {
            return null;
        }
        Product product = discountPrice.getProduct();
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
