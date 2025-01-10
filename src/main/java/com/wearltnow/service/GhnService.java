package com.wearltnow.service;

import com.wearltnow.dto.request.ghn.GhnOrderRequest;
import com.wearltnow.dto.response.ghn.GhnOrderResponse;
import com.wearltnow.dto.response.ghn.GhnShippingFeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class GhnService {
    @Value("${ghn.api.url}")
    private String apiUrl;

    @Value("${ghn.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate;

    @Autowired
    public GhnService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GhnOrderResponse createOrder(GhnOrderRequest orderRequest) {
        setDefaultValues(orderRequest);
        String createOrderUrl = apiUrl + "/create";
        // Các thiết lập khác cho orderRequest ...
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token", accessToken);
        String SHOP_ID = "194821";
        headers.set("ShopId", SHOP_ID);

        HttpEntity<GhnOrderRequest> requestEntity = new HttpEntity<>(orderRequest, headers);
        // Thực hiện yêu cầu và nhận phản hồi
        ResponseEntity<GhnOrderResponse> responseEntity = restTemplate.postForEntity(createOrderUrl, requestEntity, GhnOrderResponse.class);

        // Trả về nội dung GhnOrderResponse từ ResponseEntity
        return responseEntity.getBody();
    }

    public BigDecimal getShippingFee(String orderCode) {
        String shippingFeeApiUrl = apiUrl + "/soc";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", accessToken);
        headers.set("ShopId", "194821");

        JSONObject requestBody = new JSONObject();
        requestBody.put("order_code", orderCode);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);
        ResponseEntity<GhnShippingFeeResponse> responseEntity = restTemplate.postForEntity(
                shippingFeeApiUrl,
                requestEntity,
                GhnShippingFeeResponse.class
        );

        GhnShippingFeeResponse response = responseEntity.getBody();
        if (response != null && response.getCode() == 200) {
            GhnShippingFeeResponse.Data.Detail detail = response.getData().getDetail();

            // Trả về chỉ phí chính
            return detail.getMain_service() != null ? detail.getMain_service() : BigDecimal.ZERO;
        }

        return BigDecimal.ZERO;
    }



    private void setDefaultValues(GhnOrderRequest orderRequest) {
        orderRequest.setPayment_type_id(2);
        orderRequest.setNote("Gửi hàng quần áo");
        orderRequest.setRequired_note("KHONGCHOXEMHANG");
        orderRequest.setFrom_name("WearLTNow");
        orderRequest.setFrom_phone("0921964792");
        orderRequest.setFrom_address("102 Lê Văn Thọ, Phường 11, Gò Vấp, HCM");
        orderRequest.setFrom_ward_name("Phường 11");
        orderRequest.setFrom_district_name("Gò Vấp");
        orderRequest.setFrom_province_name("HCM");
        orderRequest.setReturn_phone("0921964792");
        orderRequest.setReturn_address("102 Lê Văn Thọ, Phường 11, Gò Vấp, HCM");
        orderRequest.setReturn_ward_code("");
        orderRequest.setReturn_district_id(null);
        orderRequest.setWeight(200);
        orderRequest.setLength(1);
        orderRequest.setWidth(9);
        orderRequest.setHeight(10);
        orderRequest.setInsurance_value(1000);
        orderRequest.setService_id(0);
        orderRequest.setPick_station_id(1444);
        orderRequest.setDeliver_station_id(null);
        orderRequest.setPick_shift(List.of(2));
    }
}
