package com.wearltnow.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GhnOrderResponse {

    @JsonProperty("code")
    private int code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private GhnOrderData data; // Một lớp con để ánh xạ dữ liệu

    // Getter và setter cho các trường
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GhnOrderData getData() {
        return data;
    }

    public void setData(GhnOrderData data) {
        this.data = data;
    }

    // Lớp con để ánh xạ dữ liệu trả về
    public static class GhnOrderData {
        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("status")
        private String status;

        @JsonProperty("order_code") // Thêm ánh xạ cho order_code
        private String orderCode;

        @JsonProperty("expected_delivery_time")
        private String expected_delivery_time;

        // Getter và setter cho các trường
        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public String getExpected_delivery_time() {
            return expected_delivery_time;
        }


        public String getOrderCode() { // Getter cho orderCode
            return orderCode;
        }

        public void setOrderCode(String orderCode) { // Setter cho orderCode
            this.orderCode = orderCode;
        }
    }
}
