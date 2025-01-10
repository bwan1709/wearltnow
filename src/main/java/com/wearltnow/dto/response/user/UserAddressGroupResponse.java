package com.wearltnow.dto.response.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAddressGroupResponse {
    Integer id;

    Long userId;

    String toName;

    String toPhone;

    String toWardCode;

    String toAddress;

    Boolean isActive;

    Integer toDistrictId;
    String toWardName;
    String toDistrictName;
    Integer toProvinceId;
    String toProvinceName;
}
