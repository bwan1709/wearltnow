package com.wearltnow.dto.request.discount;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wearltnow.model.enums.DiscountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountCodeRequest {

    @NotNull(message = "discount-code.notnull")
    private String code;

    @NotNull(message = "discount-amount.notnull")
    private BigDecimal amount;

    @NotNull(message = "discount-usage-limit.notnull")
    private Long usageLimit;

    @NotNull(message = "discount-min-order-value.notnull")
    private BigDecimal minOrderValue;

    @NotNull(message = "discount-type.notnull")
    private DiscountType type;

    @NotNull(message = "discount-start-date.notnull")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "discount-end-date.notnull")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull(message = "discount-user-group-id.notnull")
    private Long userGroupId;

}
