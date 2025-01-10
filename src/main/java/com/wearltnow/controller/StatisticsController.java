package com.wearltnow.controller;

import com.wearltnow.dto.ApiResponse;
import com.wearltnow.dto.response.statistic.ProductLeastSellResponse;
import com.wearltnow.dto.response.statistic.ProductStatsResponse;
import com.wearltnow.dto.response.statistic.StatisticsResponse;
import com.wearltnow.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // Thống kê tổng số đơn hàng có thể lọc theo ngày, khách hàng và sản phẩm
    @GetMapping("/orders")
    public ApiResponse<StatisticsResponse> getOrderStatistics(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long productId) {

        StatisticsResponse response = statisticsService.countTotalOrders(fromDate, toDate, customerId, productId);
        return ApiResponse.<StatisticsResponse>builder()
                .result(response)
                .build();
    }

    // Thống kê tổng doanh thu có thể lọc theo ngày, khách hàng và sản phẩm
    @GetMapping("/revenue")
    public ApiResponse<StatisticsResponse> getRevenueStatistics(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long productId) {

        StatisticsResponse response = statisticsService.calculateTotalRevenue(fromDate, toDate, customerId, productId);
        return ApiResponse.<StatisticsResponse>builder()
                .result(response)
                .build();
    }

    // Thống kê tổng số khách hàng
    @GetMapping("/customers")
    public ApiResponse<StatisticsResponse> getCustomerStatistics(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate
    ) {
        StatisticsResponse response = statisticsService.getCustomerStatistics(fromDate, toDate);
        return ApiResponse.<StatisticsResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/new-customers")
    public ApiResponse<StatisticsResponse> getNewCustomersStatistics(
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        if (fromDate == null) {
            fromDate = LocalDate.now();
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        StatisticsResponse response = statisticsService.countNewCustomers(fromDate, toDate);
        return ApiResponse.<StatisticsResponse>builder()
                .result(response)
                .build();
    }

    // StatisticsController.java
    @GetMapping("/product-quantity")
    public ApiResponse<StatisticsResponse> getProductQuantityStatistics(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        StatisticsResponse response = statisticsService.countTotalQuantityByProductId(productId, fromDate, toDate);
        return ApiResponse.<StatisticsResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/top-products")
    public ApiResponse<List<ProductStatsResponse>> getTopProducts(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {

        if (from == null && to == null) {
            return ApiResponse.<List<ProductStatsResponse>>builder()
                    .result(statisticsService.getAllTopProducts(null, null))
                    .build();
        }

        // Nếu có tham số từ (from) và đến (to), sử dụng chúng
        return ApiResponse.<List<ProductStatsResponse>>builder()
                .result(statisticsService.getAllTopProducts(from, to))
                .build();
    }
    @GetMapping("/quantity-by-categories")
    public List<StatisticsResponse> getTotalQuantityByCategories(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        // Gọi service để lấy thống kê tổng số lượng bán ra cho từng danh mục
        return statisticsService.countTotalQuantityByCategories(fromDate, toDate);
    }

    @GetMapping("/least-selling")
    public ApiResponse<List<ProductLeastSellResponse>> getLeastSellingProducts() {
        List<ProductLeastSellResponse> products = statisticsService.getLeastSellingProductsInStock();
        return ApiResponse.<List<ProductLeastSellResponse>>builder()
                .result(products)
                .build();
    }
}
