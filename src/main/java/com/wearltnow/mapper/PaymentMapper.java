package com.wearltnow.mapper;

import com.wearltnow.dto.request.payment.PaymentRequest;
import com.wearltnow.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    // Phương thức ánh xạ từ PaymentRequest sang Payment
    Payment toPayment(PaymentRequest paymentRequest);
    // Nếu bạn cần ánh xạ ngược lại
    PaymentRequest toPaymentRequest(Payment payment);
}