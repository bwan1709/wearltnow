package com.wearltnow.dto.response.discount;

import com.wearltnow.dto.response.user.UserGroupResponse;
import com.wearltnow.model.enums.DiscountStatus;
import com.wearltnow.model.enums.DiscountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountCodeResponse {
    Long id;
    String code;
    BigDecimal amount;
    DiscountType type;
    Long usageLimit;
    LocalDateTime startDate;
    LocalDateTime endDate;
    UserGroupResponse userGroupResponse;
    DiscountStatus status;
    BigDecimal minOrderValue;
}
