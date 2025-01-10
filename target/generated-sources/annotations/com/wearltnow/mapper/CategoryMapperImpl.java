package com.wearltnow.mapper;

import com.wearltnow.dto.request.category.CategoryCreationRequest;
import com.wearltnow.dto.response.category.CategoryResponse;
import com.wearltnow.dto.response.category.SubCategoryResponse;
import com.wearltnow.dto.response.supplier.SupplierResponse;
import com.wearltnow.model.Category;
import com.wearltnow.model.Supplier;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category toCategory(CategoryCreationRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.name( request.getName() );
        category.description( request.getDescription() );
        category.status( request.getStatus() );
        category.slug( request.getSlug() );

        return category.build();
    }

    @Override
    public CategoryResponse toCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse.CategoryResponseBuilder categoryResponse = CategoryResponse.builder();

        categoryResponse.parentId( categoryParentCategoryCategoryId( category ) );
        categoryResponse.categoryId( category.getCategoryId() );
        categoryResponse.name( category.getName() );
        categoryResponse.image( category.getImage() );
        categoryResponse.description( category.getDescription() );
        categoryResponse.status( category.getStatus() );
        categoryResponse.slug( category.getSlug() );
        categoryResponse.supplier( supplierToSupplierResponse( category.getSupplier() ) );

        categoryResponse.subCategories( mapSubCategories(category.getSubCategories()) );

        return categoryResponse.build();
    }

    @Override
    public SubCategoryResponse toSubCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        SubCategoryResponse.SubCategoryResponseBuilder subCategoryResponse = SubCategoryResponse.builder();

        subCategoryResponse.parentId( categoryParentCategoryCategoryId( category ) );
        subCategoryResponse.categoryId( category.getCategoryId() );
        subCategoryResponse.name( category.getName() );
        subCategoryResponse.slug( category.getSlug() );
        subCategoryResponse.subCategories( mapSubCategories( category.getSubCategories() ) );

        return subCategoryResponse.build();
    }

    @Override
    public void updateCategory(CategoryCreationRequest request, Category category) {
        if ( request == null ) {
            return;
        }

        category.setName( request.getName() );
        category.setDescription( request.getDescription() );
        category.setStatus( request.getStatus() );
        category.setSlug( request.getSlug() );
    }

    private Long categoryParentCategoryCategoryId(Category category) {
        if ( category == null ) {
            return null;
        }
        Category parentCategory = category.getParentCategory();
        if ( parentCategory == null ) {
            return null;
        }
        Long categoryId = parentCategory.getCategoryId();
        if ( categoryId == null ) {
            return null;
        }
        return categoryId;
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
}
