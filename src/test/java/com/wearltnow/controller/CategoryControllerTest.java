package com.wearltnow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wearltnow.dto.request.category.CategoryCreationRequest;
import com.wearltnow.dto.response.category.CategoryResponse;
import com.wearltnow.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private CategoryCreationRequest categoryCreationRequest;
    private CategoryResponse categoryResponse;
    @MockBean
    private CategoryService categoryService;


    @BeforeEach
    void setUp()
    {
        categoryCreationRequest = CategoryCreationRequest.builder()
                .name("testDanhMuc")
                .slug("test-danh-muc")
                .description("test description")
                .supplierId(1L)
                .build();


        categoryResponse = CategoryResponse.builder()
                .name("testDanhMuc")
                .slug("test-danh-muc")
                .description("test description")
                .build();
    }


    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void testCategory_create_success() throws Exception{
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(categoryCreationRequest);
        // WHEN
        Mockito.when(categoryService.createCategory(ArgumentMatchers.any()))
                .thenReturn(categoryResponse);
        // THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE) // Sử dụng đúng Content-Type
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void testCategory_update_success() throws Exception{
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(categoryCreationRequest);
        Long validId = 20L;
        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/categories/{id}", validId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    public void testCategory_delete_success() throws Exception{
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(categoryCreationRequest);
        Long validId = 20L;
        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/categories/{id}", validId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
