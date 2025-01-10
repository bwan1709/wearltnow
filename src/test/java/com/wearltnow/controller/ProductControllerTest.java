package com.wearltnow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wearltnow.dto.request.product.ProductCreationRequest;
import com.wearltnow.dto.request.product.ProductUpdateRequest;
import com.wearltnow.dto.response.product.ProductResponse;
import com.wearltnow.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private ProductCreationRequest productCreationRequest;
    private ProductResponse productResponse;
    private ProductUpdateRequest productUpdateRequest;

    @BeforeEach
    void setUp() {
        productCreationRequest = ProductCreationRequest.builder()
                .name("test")
                .categoryId(1L)
                .price(BigDecimal.valueOf(123))
                .description("test")
                .build();
        productUpdateRequest = ProductUpdateRequest.builder()
                .name("testUpdate")
                .categoryId(1L)
                .price(BigDecimal.valueOf(123))
                .description("testUpdate")
                .build();

        productResponse = ProductResponse.builder()
                .name("test")
                .price(123.0)
                .description("test")
                .build();
    }


    @Test
    @WithMockUser(username = "admin")
    public void testCreateProduct_validRequest_success() throws Exception {
        // GIVEN WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/products")
                        .param("name", "Thời trang nam")
                        .param("price", "199.99")
                        .param("description", "Thời trang dành cho người biết thưởng thức sắc đẹp 3")
                        .param("isActive", "true")
                        .param("categoryId", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin")
    public void testProduct_missingRequiredFields_fail() throws Exception {
        // GIVEN
        ProductCreationRequest invalidRequest = ProductCreationRequest.builder()
                .name(null)
                .image(null)
                .additionalImages(null)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(invalidRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/products")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE) // Sử dụng đúng Content-Type
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.name").value("Tên sản phẩm không được để trống."));
    }

    @Test
    @WithMockUser(username = "admin")
    public void testProduct_updateProduct_success() throws Exception {
        Long validId = 31L;
        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/products/{id}", validId) // Sử dụng multipart request
                        .param("name", "Thời trang nam updated")
                        .param("price", "299.99")
                        .param("description", "Updated thời trang")
                        .param("isActive", "true")
                        .param("categoryId", "2")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Cập nhật thành công."));
    }

    @Test
    @WithMockUser(username = "admin")
    public void testProduct_deleteProduct_success() throws Exception {
        Long validId = 31L;
        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/products/{id}", validId)
                        .with(request -> {
                            request.setMethod("DELETE");
                            return request;
                        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000));
    }
}
