package com.wearltnow.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductLeastSellResponse {
    private Long id;             // ID của sản phẩm
    private String name;        // Tên sản phẩm
    private Long totalQuantity;  // Tổng số lượng đã bán
    private Double totalRevenue; // Tổng doanh thu
}
