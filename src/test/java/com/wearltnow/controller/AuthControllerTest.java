package com.wearltnow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wearltnow.dto.request.auth.AuthenticationRequest;
import com.wearltnow.dto.request.auth.ForgotPasswordRequest;
import com.wearltnow.dto.request.auth.LogoutRequest;
import com.wearltnow.dto.request.auth.RegisterRequest;
import com.wearltnow.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private AuthenticationRequest authenticationRequest;
    private RegisterRequest registerRequest;
    private LogoutRequest logoutRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    public void initData(){
        authenticationRequest = AuthenticationRequest.builder().build();
        registerRequest = RegisterRequest.builder().build();
        logoutRequest = LogoutRequest.builder().build();
        forgotPasswordRequest = ForgotPasswordRequest.builder().build();
    }


    @Test
    void testLoginAccountNotFound_fail() throws Exception {
        // GIVEN
        authenticationRequest.setUsername("notFoundUsername");
        authenticationRequest.setPassword("notFoundPassword");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(authenticationRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1003));
    }

    @Test
    void testLogin_validRequest_success() throws Exception {
        // GIVEN
        authenticationRequest.setUsername("admin");
        authenticationRequest.setPassword("admin");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(authenticationRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Đăng nhập thành công."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1000));
    }

    @Test
    void testLogin_invalidRequest_fail() throws Exception {
        // GIVEN
        authenticationRequest.setUsername("admin");
        authenticationRequest.setPassword("wrongPassword");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(authenticationRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Username or Password Incorrect"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1008));
    }

    @Test
    void testRegister_emailInvalid_fail() throws Exception {
        // GIVEN
        registerRequest.setUsername("newUsername");
        registerRequest.setPassword("newPassword");
        registerRequest.setEmail("emailNoGmail");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(registerRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.email").value("Địa chỉ email không hợp lệ."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(422));
    }

    @Test
    void testRegister_passwordInvalid_fail() throws Exception {
        // GIVEN
        registerRequest.setUsername("newUsername");
        registerRequest.setPassword("1");
        registerRequest.setEmail("emailNoGmail@gmail.com");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(registerRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.password").value("Mật khẩu phải có ít nhất 8 ký tự."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(422));
    }

    @Test
    void testForgotPassword_validRequest_success() throws Exception {
        // GIVEN
        forgotPasswordRequest.setEmail("hoangbinhquan179@gmail.com");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String content = mapper.writeValueAsString(forgotPasswordRequest);

        // WHEN THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Gửi OTP thành công, vui lòng kiểm tra email của bạn."));
    }
}
