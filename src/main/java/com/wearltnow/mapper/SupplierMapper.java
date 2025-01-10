package com.wearltnow.mapper;

import com.wearltnow.dto.request.supplier.SupplierCreationRequest;
import com.wearltnow.dto.request.supplier.SupplierUpdateRequest;
import com.wearltnow.dto.response.supplier.SupplierResponse;
import com.wearltnow.model.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toSupplier(SupplierCreationRequest supplier);

    SupplierResponse toSupplierResponse(Supplier supplier);

    void updateSupplier(@MappingTarget Supplier supplier, SupplierUpdateRequest request);
}
