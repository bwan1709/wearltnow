package com.wearltnow.dto.request.order;

import com.wearltnow.model.CartItem;
import lombok.Data;

import java.util.List;

@Data
public class CheckoutRequest {
    private Long userId;
    private Long paymentMethodId;
    private List<CartItem> items;
    private int serviceTypeId;
    private String discountCode;
}
