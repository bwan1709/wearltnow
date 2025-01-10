package com.wearltnow.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GhnProduct {
    String name;
    String code;
    int quantity;
    int price;
}
