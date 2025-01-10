package com.wearltnow.dto.request.order;

import com.wearltnow.dto.request.user.AddressRequest;
import lombok.Data;

@Data
public class CheckoutDto {
    private CheckoutRequest checkoutRequest;
    private AddressRequest addressRequest;
}
