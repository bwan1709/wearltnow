package com.wearltnow.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductStatsResponse {
    private String name;
    private Long totalQuantity;
    private Double totalRevenue;
}
