package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.supplier.SupplierCreationRequest;
import com.wearltnow.dto.request.supplier.SupplierUpdateRequest;
import com.wearltnow.dto.response.supplier.SupplierResponse;
import com.wearltnow.service.SupplierService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierController {
    SupplierService supplierService;

    @PostMapping
    public ApiResponse<SupplierResponse> create(@RequestBody SupplierCreationRequest request){
        return ApiResponse.<SupplierResponse>builder()
                .result(supplierService.create(request))
                .build();
    }

    @GetMapping()
    ApiResponse<PageResponse<SupplierResponse>> getAllSuplliers
            (@RequestParam(value = "page", required = false, defaultValue = "1") int page,
             @RequestParam(value = "size", required = false, defaultValue = "8 ") int size) {
        return ApiResponse.<PageResponse<SupplierResponse>>builder()
                .result(supplierService.getAllSuppliers(page,size))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<SupplierResponse> getSuplliers(@PathVariable("id") Long id) {
        return ApiResponse.<SupplierResponse>builder()
                .result(supplierService.getSupplierById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<SupplierResponse> updateSuplliers(@RequestBody @Valid SupplierUpdateRequest request, @PathVariable("id") Long id) {
        return ApiResponse.<SupplierResponse>builder()
                .result(supplierService.updateSupplier(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<SupplierResponse> deleteSuplier(@PathVariable("id") Long id) {
        supplierService.deleteSupplier(id);
        return ApiResponse.<SupplierResponse>builder()
                .message("Deleted supplier")
                .build();
    }
}
