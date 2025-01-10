package com.wearltnow.dto.request.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressGroupUpdationRequest {
    @NotNull(message = "user-id.notnull")
    Long userId;

    @NotNull(message = "to-name.notnull")
    String toName;

    @NotNull(message = "to-phone.notnull")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "to-phone.invalid")
    String toPhone;

    @NotNull(message = "to-ward-code.notnull")
    String toWardCode;

    @NotNull(message = "to-address.notnull")
    String toAddress;

    @NotNull(message = "to-district-id.notnull")
    Integer toDistrictId;

    @NotNull(message = "to-ward-name.notnull")
    String toWardName;

    @NotNull(message = "to-district-name.notnull")
    String toDistrictName;

    @NotNull(message = "to-province-id.notnull")
    Integer toProvinceId;

    @NotNull(message = "to-province-name.notnull")
    String toProvinceName;
}
