package com.wearltnow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearltnow.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void initData() {
        // You can initialize any mock data here if needed
    }

    @Test
    @WithMockUser(username = "admin")
    void testAddToCart_validRequest_success() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setProductId(20L);
        cartItem.setSize("XXL");
        cartItem.setColor("Black");
        cartItem.setQuantity(3);

        mockMvc.perform(MockMvcRequestBuilders.post("/v0/cart/save")
                        .param("userId", "1")  // assuming a userId for test
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(cartItem))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Thêm sản phẩm vào giỏ hàng thành công."));
    }

    @Test
    @WithMockUser(username = "admin")
    void testAddToCart_invalidRequest_fail() throws Exception {
        // Try to update a CartItem that is not in the cart
        CartItem cartItem = new CartItem();
        cartItem.setProductId(12312421L);
        cartItem.setSize("XXL");
        cartItem.setColor("Black");
        cartItem.setQuantity(3);

        mockMvc.perform(MockMvcRequestBuilders.post("/v0/cart/save")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(cartItem))))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Product Not Found"));
    }

    @Test
    void testAddToCart_notLogin_fail() throws Exception {
        // Try to update a CartItem that is not in the cart
        CartItem cartItem = new CartItem();
        cartItem.setProductId(18L);
        cartItem.setSize("XXL");
        cartItem.setColor("Black");
        cartItem.setQuantity(3);

        mockMvc.perform(MockMvcRequestBuilders.post("/v0/cart/save")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(cartItem))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Unauthenticated"));
    }

    @Test
    @WithMockUser(username = "admin")
    void testAddToCart_notEnoughQty_fail() throws Exception {
        // Try to update a CartItem that is not in the cart
        CartItem cartItem = new CartItem();
        cartItem.setProductId(18L);
        cartItem.setSize("XXL");
        cartItem.setColor("Black");
        cartItem.setQuantity(10000);

        mockMvc.perform(MockMvcRequestBuilders.post("/v0/cart/save")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(cartItem))))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Not enough item in inventory"));
    }

}
