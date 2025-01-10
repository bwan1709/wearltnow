package com.wearltnow.service;

import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.supplier.SupplierCreationRequest;
import com.wearltnow.dto.request.supplier.SupplierUpdateRequest;
import com.wearltnow.dto.response.supplier.SupplierResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.SupplierMapper;
import com.wearltnow.model.Supplier;
import com.wearltnow.repository.SupplierRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierService {
    SupplierRepository supplierRepository;
    SupplierMapper supplierMapper;

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public PageResponse<SupplierResponse> getAllSuppliers(int page, int size){
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = supplierRepository.findAllByDeletedFalse(pageable);
        return PageResponse.<SupplierResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(supplierMapper::toSupplierResponse).toList())
                .build();
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public SupplierResponse getSupplierById(long id){
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        return supplierMapper.toSupplierResponse(supplier);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public SupplierResponse create(SupplierCreationRequest request){
        Supplier supplier = supplierMapper.toSupplier(request);
        supplier = supplierRepository.save(supplier);
        return supplierMapper.toSupplierResponse(supplier);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public SupplierResponse updateSupplier(Long id, SupplierUpdateRequest request){
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        supplierMapper.updateSupplier(supplier, request);
        return supplierMapper.toSupplierResponse(supplierRepository.save(supplier));
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void deleteSupplier(Long id){
        supplierRepository.findBySupplierIdAndDeletedFalse(id).orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        supplierRepository.deleteById(id);
    }
}
