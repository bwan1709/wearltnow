package com.wearltnow.service;

import com.wearltnow.dto.response.statistic.ProductLeastSellResponse;
import com.wearltnow.dto.response.statistic.ProductStatsResponse;
import com.wearltnow.dto.response.statistic.RevenueAndProfitResponse;
import com.wearltnow.dto.response.statistic.StatisticsResponse;
import com.wearltnow.model.Order;
import com.wearltnow.model.OrderDetail;
import com.wearltnow.model.ProductInventory;
import com.wearltnow.repository.OrderDetailRepository;
import com.wearltnow.repository.StatisticsRepository;
import com.wearltnow.specification.OrderSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public StatisticsService(StatisticsRepository statisticsRepository, OrderDetailRepository orderDetailRepository) {
        this.statisticsRepository = statisticsRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    // Thống kê tổng số đơn hàng có thể lọc theo ngày, khách hàng và sản phẩm
    @PreAuthorize("hasRole('DIRECTOR')")
    public StatisticsResponse countTotalOrders(LocalDate fromDate, LocalDate toDate, Long customerId, Long productId) {
        Specification<Order> spec = Specification.where(null);

        if (fromDate != null && toDate != null) {
            spec = spec.and(OrderSpecification.createdBetween(fromDate, toDate));
        }
        if (customerId != null) {
            spec = spec.and(OrderSpecification.hasCustomerId(customerId));
        }
        if (productId != null) {
            spec = spec.and(OrderSpecification.hasProductId(productId));
        }

        Long totalOrders = statisticsRepository.count(spec);
        return new StatisticsResponse("Tổng đơn hàng",totalOrders);
    }

    // Thống kê tổng doanh thu có thể lọc theo ngày, khách hàng và sản phẩm
    @PreAuthorize("hasRole('DIRECTOR')")
    public StatisticsResponse calculateTotalRevenue(LocalDate fromDate, LocalDate toDate, Long customerId, Long productId) {
        Specification<Order> spec = Specification.where(null);

        if (fromDate != null && toDate != null) {
            spec = spec.and(OrderSpecification.createdBetween(fromDate, toDate));
        }
        if (customerId != null) {
            spec = spec.and(OrderSpecification.hasCustomerId(customerId));
        }
        if (productId != null) {
            spec = spec.and(OrderSpecification.hasProductId(productId));
        }

        List<Order> orders = statisticsRepository.findAll(spec);
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPurchaseCost = orders.stream()
                .flatMap(order -> order.getOrderDetails().stream())
                .map(detail -> {
                    BigDecimal purchasePrice = detail.getProduct().getProductInventories().stream()
                            .map(ProductInventory::getPurchasePrice)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);
                    return purchasePrice.multiply(BigDecimal.valueOf(detail.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = totalRevenue.subtract(totalPurchaseCost);
        RevenueAndProfitResponse revenueAndProfitResponse = new RevenueAndProfitResponse(totalRevenue, totalProfit);

        return new StatisticsResponse("Tổng doanh thu" , revenueAndProfitResponse);
    }

    // Thống kê tổng số khách hàng
    @PreAuthorize("hasRole('DIRECTOR')")
    public StatisticsResponse getCustomerStatistics(LocalDate fromDate, LocalDate toDate) {
        Long totalCustomers = statisticsRepository.countDistinctUserId(fromDate, toDate);
        return new StatisticsResponse("Tổng khách hàng", totalCustomers);
    }

    @PreAuthorize("hasRole('DIRECTOR')")
    public StatisticsResponse countNewCustomers(LocalDate fromDate, LocalDate toDate) {
        Long newCustomers = statisticsRepository.countNewCustomers(fromDate, toDate);
        return new StatisticsResponse("Khách hàng mới", newCustomers);
    }

    // StatisticsService.java
    @PreAuthorize("hasRole('DIRECTOR')")
    public StatisticsResponse countTotalQuantityByProductId(Long productId, LocalDate fromDate, LocalDate toDate) {
        Specification<Order> spec = Specification.where(null);

        // Nếu không truyền ngày thì mặc định là từ ngày đầu tiên đến ngày cuối cùng có trong dữ liệu
        if (fromDate == null) {
            fromDate = statisticsRepository.findEarliestOrderDate().orElse(LocalDate.now());
        }
        if (toDate == null) {
            toDate = statisticsRepository.findLatestOrderDate().orElse(LocalDate.now());
        }

        // Áp dụng bộ lọc thời gian
        spec = spec.and(OrderSpecification.createdBetween(fromDate, toDate));

        // Áp dụng bộ lọc theo productId
        if (productId != null) {
            spec = spec.and(OrderSpecification.hasProductId(productId));
        }

        List<Order> orders = statisticsRepository.findAll(spec);

        // Tính tổng số lượng sản phẩm theo productId
        Long totalQuantity = orders.stream()
                .flatMap(order -> order.getOrderDetails().stream())  // Lấy orderDetails từ từng order
                .filter(detail -> detail.getProduct().getProductId().equals(productId))  // Lọc productId
                .mapToLong(detail -> detail.getQuantity())  // Lấy số lượng từ detail
                .sum();

        return new StatisticsResponse("Tổng số lượng sản phẩm", totalQuantity);
    }

    @PreAuthorize("hasRole('DIRECTOR')")
    public List<ProductStatsResponse> getAllTopProducts(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime end = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
        if (startDate == null && endDate == null) {
            return orderDetailRepository.findProductSalesStats();
        }
        return orderDetailRepository.findProductSalesStatsByDateRange(start, end);
    }

    @PreAuthorize("hasRole('DIRECTOR')")
    public List<StatisticsResponse> countTotalQuantityByCategories(LocalDate fromDate, LocalDate toDate) {
        Specification<Order> spec = Specification.where(null);

        // Nếu không truyền ngày thì mặc định là từ ngày đầu tiên đến ngày cuối cùng có trong dữ liệu
        if (fromDate == null) {
            fromDate = statisticsRepository.findEarliestOrderDate().orElse(LocalDate.now());
        }
        if (toDate == null) {
            toDate = statisticsRepository.findLatestOrderDate().orElse(LocalDate.now());
        }

        // Áp dụng bộ lọc thời gian
        spec = spec.and(OrderSpecification.createdBetween(fromDate, toDate));

        // Lấy tất cả đơn hàng trong khoảng thời gian đã chọn
        List<Order> orders = statisticsRepository.findAll(spec);

        // Tạo một map để chứa thống kê theo từng danh mục
        Map<Long, Long> categorySalesMap = new HashMap<>();

        // Duyệt qua các đơn hàng để tính tổng số lượng theo danh mục
        for (Order order : orders) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Long categoryId = detail.getProduct().getCategory().getCategoryId();
                categorySalesMap.put(categoryId, categorySalesMap.getOrDefault(categoryId, 0L) + detail.getQuantity());
            }
        }

        // Tạo danh sách StatisticsResponse với tên danh mục thay vì categoryId
        List<StatisticsResponse> categoryStats = categorySalesMap.entrySet().stream()
                .map(entry -> {
                    // Lấy tên danh mục từ categoryId
                    String categoryName = statisticsRepository.findCategoryNameById(entry.getKey())
                            .orElse("Danh mục không xác định");
                    return new StatisticsResponse(categoryName, entry.getValue());
                })
                .collect(Collectors.toList());

        return categoryStats;
    }

    // find những sản phẩm chua ban dc hoac ban e
    @PreAuthorize("hasRole('DIRECTOR')")
    public List<ProductLeastSellResponse> getLeastSellingProductsInStock() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return orderDetailRepository.findLeastSellingProductsInStock(thirtyDaysAgo);
    }
}
