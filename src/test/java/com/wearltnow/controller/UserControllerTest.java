package com.wearltnow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wearltnow.dto.request.user.UserCreationRequest;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LocalDate   dob;

    @BeforeEach
    public void initData(){
        dob = LocalDate.of(2000, 1, 1);
        userCreationRequest = UserCreationRequest.builder()
                .username("test")
                .firstname("test")
                .lastname("test")
                .gender(true)
                .phone("09824124512")
                .email("test@test.com")
                .password("12345678")
                .dob(dob)
                .build();
                userResponse = UserResponse.builder()
                        .userId(Long.parseLong("12"))
                        .username("test")
                        .firstname("test")
                        .lastname("test")
                        .email("test@test.com")
                        .dob(dob)
                        .build();
    }
    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any()))
                .thenReturn(userResponse);
        // WHEN THEN
            mockMvc.perform(MockMvcRequestBuilders
                    .post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(content))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("code")
                            .value(1000))
                    .andExpect(MockMvcResultMatchers.jsonPath("result.userId")
                            .value(Long.parseLong("12"))
            );
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        // GIVEN
        userCreationRequest.setUsername("te");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(userCreationRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(422))
                .andExpect(MockMvcResultMatchers.jsonPath("message")
                        .value("Có lỗi xảy ra khi xác thực.")
                );
    }
}

