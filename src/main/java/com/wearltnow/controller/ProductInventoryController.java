package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.request.product.ProductInventoryCreationRequest;
import com.wearltnow.dto.request.product.ProductInventoryUpdationRequest;
import com.wearltnow.dto.response.product.ProductInventoryResponse;
import com.wearltnow.service.ProductInventoryService;
import com.wearltnow.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Slf4j
public class ProductInventoryController {
    private final ProductInventoryService productInventoryService;
    private final MessageUtils messageUtils;


    @GetMapping
    public ApiResponse<List<ProductInventoryResponse>> getAllInventories() {
        List<ProductInventoryResponse> responses = productInventoryService.getAllInventories();
        return ApiResponse.<List<ProductInventoryResponse>>builder()
                .result(responses)
                .build();
    }

    // Thêm tồn kho mới cho sản phẩm
    @PostMapping
    public ApiResponse<ProductInventoryResponse> addInventory(@RequestBody ProductInventoryCreationRequest request) {
        ProductInventoryResponse response = productInventoryService.addInventory(request);
        return ApiResponse.<ProductInventoryResponse>builder()
                .result(response)
                .build();
    }

    // Cập nhật thông tin tồn kho
    @PutMapping("/{inventoryId}")
    public ApiResponse<ProductInventoryResponse> updateInventory(
            @PathVariable Long inventoryId,
            @RequestBody ProductInventoryUpdationRequest request) {
        ProductInventoryResponse response = productInventoryService.updateInventory(inventoryId, request);
        return ApiResponse.<ProductInventoryResponse>builder()
                .result(response)
                .build();
    }

    // Xóa tồn kho
    @DeleteMapping("/{inventoryId}")
    public ApiResponse<Void> deleteInventory(@PathVariable Long inventoryId) {
        productInventoryService.deleteInventory(inventoryId);
        return ApiResponse.<Void>builder()
                .message(messageUtils.getAttributeMessage("delete.success"))
                .build();
    }

    // Lấy danh sách tồn kho theo sản phẩm
    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductInventoryResponse>> getInventoriesByProduct(@PathVariable Long productId) {
        List<ProductInventoryResponse> responses = productInventoryService.getInventoriesByProduct(productId);
        return ApiResponse.<List<ProductInventoryResponse>>builder()
                .result(responses)
                .build();
    }

    // Lấy tồn kho theo ID
    @GetMapping("/{inventoryId}")
    public ApiResponse<ProductInventoryResponse> getInventoryById(@PathVariable Long inventoryId) {
        ProductInventoryResponse response = productInventoryService.getInventoryById(inventoryId);
        return ApiResponse.<ProductInventoryResponse>builder()
                .result(response)
                .build();
    }




    @GetMapping("/export")
    public ResponseEntity<byte[]> exportInventoriesToExcel() {
        List<ProductInventoryResponse> inventoryList = productInventoryService.getAllInventories();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventories");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("InventoryId");
            headerRow.createCell(2).setCellValue("Product ID");
            headerRow.createCell(3).setCellValue("Quantity");
            headerRow.createCell(4).setCellValue("Size");
            headerRow.createCell(5).setCellValue("Color");

            // Điền dữ liệu
            int rowIdx = 1;
            for (ProductInventoryResponse inventory : inventoryList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);  // ID (row number)
                row.createCell(1).setCellValue(inventory.getProductInventoryId());
                row.createCell(2).setCellValue(inventory.getProductId());
                row.createCell(3).setCellValue(inventory.getQuantity());
                row.createCell(4).setCellValue(inventory.getSize());
                row.createCell(5).setCellValue(inventory.getColor());
            }

            // Ghi dữ liệu vào mảng byte
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "Báo cáo tổn kho.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error while exporting inventories to Excel", e);
        }
    }
}
