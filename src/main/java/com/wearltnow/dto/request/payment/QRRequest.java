package com.wearltnow.dto.request.payment;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class QRRequest {
    private Long paymentId;
    private BigDecimal totalAmount;

    private String accountNo = "877263982";
    private String accountName = "NGUYEN TRUNG HIEU";
    private String acqId = "970422";
    private String template = "uRwEfpr";
    private String addInfo;
    private String amount;
}
