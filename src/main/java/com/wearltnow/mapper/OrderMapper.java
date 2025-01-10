package com.wearltnow.mapper;

import com.wearltnow.dto.response.order.OrderDetailResponse;
import com.wearltnow.dto.response.order.OrderResponse;
import com.wearltnow.model.Order;
import com.wearltnow.model.OrderDetail;
import org.mapstruct.*;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "orderAmount", target = "totalAmount") // ánh xạ orderAmount thành totalAmount
    @Mapping(source = "status", target = "status")
    @Mapping(source = "orderDetails", target = "orderDetails")
    @Mapping(source = "orderTotal", target = "totalOrder") // ánh xạ orderAmount thành totalAmount
    @Mapping(source = "payment.paymentStatus", target = "payment_status")
    @Mapping(source = "payment.paymentId", target = "paymentId")
    OrderResponse toOrderResponseDTO(Order order);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.name", target = "name")
    @Mapping(source = "product.image", target = "image")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);
}
