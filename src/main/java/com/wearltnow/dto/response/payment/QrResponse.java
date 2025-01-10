package com.wearltnow.dto.response.payment;

import lombok.Data;

@Data
public class QrResponse {
    private String qrCodeUrl;
    private String amount;
    private String addInfo;

}
