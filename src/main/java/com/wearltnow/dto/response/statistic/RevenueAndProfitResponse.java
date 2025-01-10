package com.wearltnow.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RevenueAndProfitResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
}
