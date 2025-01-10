package com.wearltnow.dto.request.price;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceRequest {
    String code;
    String name;
    LocalDate startDate;
    LocalDate endDate;
}
