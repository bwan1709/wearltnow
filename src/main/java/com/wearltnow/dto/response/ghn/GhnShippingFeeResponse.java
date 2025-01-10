package com.wearltnow.dto.response.ghn;

import java.math.BigDecimal;
import java.util.List;

public class GhnShippingFeeResponse {
    private int code;
    private String message;
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String order_code;
        private Detail detail;
        private List<Payment> payment;

        public String getOrder_code() {
            return order_code;
        }

        public void setOrder_code(String order_code) {
            this.order_code = order_code;
        }

        public Detail getDetail() {
            return detail;
        }

        public void setDetail(Detail detail) {
            this.detail = detail;
        }

        public List<Payment> getPayment() {
            return payment;
        }

        public void setPayment(List<Payment> payment) {
            this.payment = payment;
        }

        public static class Detail {
            private BigDecimal main_service;
            private BigDecimal insurance;
            private BigDecimal station_do;
            private BigDecimal station_pu;
            private BigDecimal returnFee;
            private BigDecimal r2s;
            private BigDecimal coupon;

            public BigDecimal getMain_service() {
                return main_service;
            }

            public void setMain_service(BigDecimal main_service) {
                this.main_service = main_service;
            }

            public BigDecimal getInsurance() {
                return insurance;
            }

            public void setInsurance(BigDecimal insurance) {
                this.insurance = insurance;
            }

            public BigDecimal getStation_do() {
                return station_do;
            }

            public void setStation_do(BigDecimal station_do) {
                this.station_do = station_do;
            }

            public BigDecimal getStation_pu() {
                return station_pu;
            }

            public void setStation_pu(BigDecimal station_pu) {
                this.station_pu = station_pu;
            }

            public BigDecimal getReturnFee() {
                return returnFee;
            }

            public void setReturnFee(BigDecimal returnFee) {
                this.returnFee = returnFee;
            }

            public BigDecimal getR2s() {
                return r2s;
            }

            public void setR2s(BigDecimal r2s) {
                this.r2s = r2s;
            }

            public BigDecimal getCoupon() {
                return coupon;
            }

            public void setCoupon(BigDecimal coupon) {
                this.coupon = coupon;
            }
        }

        public static class Payment {
            private BigDecimal value;
            private int payment_type;

            public BigDecimal getValue() {
                return value;
            }

            public void setValue(BigDecimal value) {
                this.value = value;
            }

            public int getPayment_type() {
                return payment_type;
            }

            public void setPayment_type(int payment_type) {
                this.payment_type = payment_type;
            }
        }
    }
}
