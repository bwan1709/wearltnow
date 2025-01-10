package com.wearltnow.mapper;

import com.wearltnow.dto.response.auth.PermissionResponse;
import com.wearltnow.dto.response.auth.RoleResponse;
import com.wearltnow.dto.response.category.CategoryResponse;
import com.wearltnow.dto.response.category.SubCategoryResponse;
import com.wearltnow.dto.response.product.ProductFavoriteResponse;
import com.wearltnow.dto.response.product.ProductResponse;
import com.wearltnow.dto.response.supplier.SupplierResponse;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.model.Category;
import com.wearltnow.model.Permission;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductFavorite;
import com.wearltnow.model.Role;
import com.wearltnow.model.Supplier;
import com.wearltnow.model.User;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProductFavoriteMapperImpl implements ProductFavoriteMapper {

    @Override
    public ProductFavoriteResponse toResponse(ProductFavorite productComment) {
        if ( productComment == null ) {
            return null;
        }

        ProductFavoriteResponse.ProductFavoriteResponseBuilder productFavoriteResponse = ProductFavoriteResponse.builder();

        productFavoriteResponse.product( productToProductResponse( productComment.getProduct() ) );
        productFavoriteResponse.user( userToUserResponse( productComment.getUser() ) );

        return productFavoriteResponse.build();
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

    protected ProductResponse productToProductResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.productId( product.getProductId() );
        productResponse.name( product.getName() );
        productResponse.price( product.getPrice() );
        productResponse.image( product.getImage() );
        productResponse.description( product.getDescription() );
        productResponse.category( categoryToCategoryResponse( product.getCategory() ) );

        return productResponse.build();
    }

    protected PermissionResponse permissionToPermissionResponse(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        PermissionResponse.PermissionResponseBuilder permissionResponse = PermissionResponse.builder();

        permissionResponse.name( permission.getName() );
        permissionResponse.description( permission.getDescription() );

        return permissionResponse.build();
    }

    protected Set<PermissionResponse> permissionSetToPermissionResponseSet(Set<Permission> set) {
        if ( set == null ) {
            return null;
        }

        Set<PermissionResponse> set1 = new LinkedHashSet<PermissionResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Permission permission : set ) {
            set1.add( permissionToPermissionResponse( permission ) );
        }

        return set1;
    }

    protected RoleResponse roleToRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();

        roleResponse.name( role.getName() );
        roleResponse.description( role.getDescription() );
        roleResponse.permissions( permissionSetToPermissionResponseSet( role.getPermissions() ) );

        return roleResponse.build();
    }

    protected Set<RoleResponse> roleSetToRoleResponseSet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleResponse> set1 = new LinkedHashSet<RoleResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleToRoleResponse( role ) );
        }

        return set1;
    }

    protected UserResponse userToUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.userId( user.getUserId() );
        userResponse.username( user.getUsername() );
        userResponse.lastname( user.getLastname() );
        userResponse.firstname( user.getFirstname() );
        userResponse.phone( user.getPhone() );
        userResponse.email( user.getEmail() );
        userResponse.gender( user.getGender() );
        userResponse.dob( user.getDob() );
        userResponse.roles( roleSetToRoleResponseSet( user.getRoles() ) );

        return userResponse.build();
    }
}
