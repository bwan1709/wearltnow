package com.wearltnow.mapper;

import com.wearltnow.dto.request.supplier.SupplierCreationRequest;
import com.wearltnow.dto.request.supplier.SupplierUpdateRequest;
import com.wearltnow.dto.response.supplier.SupplierResponse;
import com.wearltnow.model.Supplier;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class SupplierMapperImpl implements SupplierMapper {

    @Override
    public Supplier toSupplier(SupplierCreationRequest supplier) {
        if ( supplier == null ) {
            return null;
        }

        Supplier.SupplierBuilder supplier1 = Supplier.builder();

        supplier1.description( supplier.getDescription() );
        supplier1.email( supplier.getEmail() );
        supplier1.contactPerson( supplier.getContactPerson() );
        supplier1.name( supplier.getName() );
        supplier1.website( supplier.getWebsite() );
        supplier1.phone( supplier.getPhone() );
        supplier1.taxCode( supplier.getTaxCode() );

        return supplier1.build();
    }

    @Override
    public SupplierResponse toSupplierResponse(Supplier supplier) {
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

    @Override
    public void updateSupplier(Supplier supplier, SupplierUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        supplier.setDescription( request.getDescription() );
        supplier.setEmail( request.getEmail() );
        supplier.setContactPerson( request.getContactPerson() );
        supplier.setName( request.getName() );
        supplier.setWebsite( request.getWebsite() );
        supplier.setPhone( request.getPhone() );
        supplier.setTaxCode( request.getTaxCode() );
    }
}
