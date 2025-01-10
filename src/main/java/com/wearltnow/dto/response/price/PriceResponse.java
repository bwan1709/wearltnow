package com.wearltnow.dto.response.price;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceResponse {
    Long id;
    String code;
    String name;
    LocalDate startDate;
    LocalDate endDate;
}
