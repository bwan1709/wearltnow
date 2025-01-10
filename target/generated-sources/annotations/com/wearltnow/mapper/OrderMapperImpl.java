package com.wearltnow.mapper;

import com.wearltnow.dto.response.order.OrderDetailResponse;
import com.wearltnow.dto.response.order.OrderResponse;
import com.wearltnow.model.Order;
import com.wearltnow.model.OrderDetail;
import com.wearltnow.model.Payment;
import com.wearltnow.model.Product;
import com.wearltnow.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toOrderResponseDTO(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.userId( orderUserUserId( order ) );
        orderResponse.totalAmount( order.getOrderAmount() );
        orderResponse.status( order.getStatus() );
        orderResponse.orderDetails( orderDetailListToOrderDetailResponseList( order.getOrderDetails() ) );
        orderResponse.totalOrder( order.getOrderTotal() );
        orderResponse.payment_status( orderPaymentPaymentStatus( order ) );
        orderResponse.paymentId( orderPaymentPaymentId( order ) );
        orderResponse.orderId( order.getOrderId() );
        orderResponse.order_code( order.getOrder_code() );
        orderResponse.discountAmount( order.getDiscountAmount() );
        orderResponse.shippingFee( order.getShippingFee() );
        orderResponse.expected_delivery_time( order.getExpected_delivery_time() );
        orderResponse.createdAt( order.getCreatedAt() );

        return orderResponse.build();
    }

    @Override
    public OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail) {
        if ( orderDetail == null ) {
            return null;
        }

        OrderDetailResponse orderDetailResponse = new OrderDetailResponse();

        orderDetailResponse.setProductId( orderDetailProductProductId( orderDetail ) );
        orderDetailResponse.setName( orderDetailProductName( orderDetail ) );
        orderDetailResponse.setImage( orderDetailProductImage( orderDetail ) );
        orderDetailResponse.setPrice( orderDetail.getPrice() );
        orderDetailResponse.setQuantity( orderDetail.getQuantity() );
        orderDetailResponse.setColor( orderDetail.getColor() );
        orderDetailResponse.setSize( orderDetail.getSize() );

        return orderDetailResponse;
    }

    private Long orderUserUserId(Order order) {
        if ( order == null ) {
            return null;
        }
        User user = order.getUser();
        if ( user == null ) {
            return null;
        }
        Long userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    protected List<OrderDetailResponse> orderDetailListToOrderDetailResponseList(List<OrderDetail> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderDetailResponse> list1 = new ArrayList<OrderDetailResponse>( list.size() );
        for ( OrderDetail orderDetail : list ) {
            list1.add( toOrderDetailResponse( orderDetail ) );
        }

        return list1;
    }

    private String orderPaymentPaymentStatus(Order order) {
        if ( order == null ) {
            return null;
        }
        Payment payment = order.getPayment();
        if ( payment == null ) {
            return null;
        }
        String paymentStatus = payment.getPaymentStatus();
        if ( paymentStatus == null ) {
            return null;
        }
        return paymentStatus;
    }

    private Long orderPaymentPaymentId(Order order) {
        if ( order == null ) {
            return null;
        }
        Payment payment = order.getPayment();
        if ( payment == null ) {
            return null;
        }
        Long paymentId = payment.getPaymentId();
        if ( paymentId == null ) {
            return null;
        }
        return paymentId;
    }

    private Long orderDetailProductProductId(OrderDetail orderDetail) {
        if ( orderDetail == null ) {
            return null;
        }
        Product product = orderDetail.getProduct();
        if ( product == null ) {
            return null;
        }
        Long productId = product.getProductId();
        if ( productId == null ) {
            return null;
        }
        return productId;
    }

    private String orderDetailProductName(OrderDetail orderDetail) {
        if ( orderDetail == null ) {
            return null;
        }
        Product product = orderDetail.getProduct();
        if ( product == null ) {
            return null;
        }
        String name = product.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String orderDetailProductImage(OrderDetail orderDetail) {
        if ( orderDetail == null ) {
            return null;
        }
        Product product = orderDetail.getProduct();
        if ( product == null ) {
            return null;
        }
        String image = product.getImage();
        if ( image == null ) {
            return null;
        }
        return image;
    }
}
