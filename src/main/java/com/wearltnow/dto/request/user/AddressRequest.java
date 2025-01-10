package com.wearltnow.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressRequest {
    @NotNull(message = "to-name.notNull")
    private String toName;
    @NotNull(message = "to-phone.notNull")
    private String toPhone;
    @NotNull(message = "to-ward-code.notNull")
    private String toWardCode;
    @NotNull(message = "to-address.notNull")
    private String toAddress;
    @NotNull(message = "to-district-id.notNull")
    private int toDistrictId;

}
