package com.wearltnow.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin")
    void testViewOrder_orderNotFound_fail() throws Exception {
        // GIVEN: Đơn hàng không tồn tại
        Integer invalidOrderId = 999;

        // WHEN THEN: Gọi API confirm đơn hàng với orderId không hợp lệ
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/detail/{orderId}", invalidOrderId))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Order Not Found"));
    }

    @Test
    void testViewOrder_notLogin_fail() throws Exception {
        // GIVEN:
        Integer invalidOrderId = 427;

        // WHEN THEN: Gọi API confirm đơn hàng với orderId không hợp lệ
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/detail/{orderId}", invalidOrderId))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Unauthenticated"));
    }

    @Test
    @WithMockUser(username = "admin")
    void testViewOrder_validRequest_success() throws Exception {
        // GIVEN:
        Integer invalidOrderId = 427;
        // WHEN THEN:
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/detail/{orderId}", invalidOrderId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void testUpdateOrder_validStatus_success() throws Exception {
        // GIVEN
        Long validOrderId = 426L;
        String newStatus = "CONFIRMED";
        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/orders/{orderId}/status", validOrderId)
                        .param("newStatus", newStatus)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Cập nhật thành công."));
    }
}
