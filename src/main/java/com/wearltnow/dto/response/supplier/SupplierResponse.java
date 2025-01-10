package com.wearltnow.dto.response.supplier;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierResponse {
    Long supplierId;
    String description;
    String email;
    String contactPerson;
    String name;
    String website;
    String phone;
    String taxCode;
}
