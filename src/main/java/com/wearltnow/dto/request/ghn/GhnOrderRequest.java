    package com.wearltnow.dto.request.ghn;

    import com.wearltnow.model.GhnProduct;
    import lombok.Data;

    import java.util.List;

    @Data
    public class GhnOrderRequest {
        private int payment_type_id;
        private String order_code;
        private String note;
        private String required_note;
        private String from_name;
        private String from_phone;
        private String from_address;
        private String from_ward_name;
        private String from_district_name;
        private String from_province_name;
        private String return_phone;
        private String return_address;
        private String return_ward_code;
        private Integer return_district_id;
        private String client_order_code;
        private String to_name;
        private String to_phone;
        private String to_address;
        private String to_ward_code;
        private Integer to_district_id;
        private int cod_amount;
        private String content;
        private int weight;
        private int length;
        private int width;
        private int height;
        private Integer pick_station_id;
        private Integer deliver_station_id;
        private int insurance_value;
        private int service_id;
        private int service_type_id;
        private String coupon;
        private List<Integer> pick_shift;
        private List<GhnProduct> items;
    }
