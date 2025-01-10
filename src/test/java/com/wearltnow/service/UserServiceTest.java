package com.wearltnow.service;

import com.wearltnow.dto.request.user.UserCreationRequest;
import com.wearltnow.dto.request.user.UserUpdateRequest;
import com.wearltnow.dto.response.user.UserResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.model.User;
import com.wearltnow.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob;
    @BeforeEach
    public void initData() {
        dob = LocalDate.of(2000, 1, 1);
        userCreationRequest = UserCreationRequest.builder()
                .username("tien")
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
        user = User.builder()
                .userId(Long.parseLong("12"))
                .username("test")
                .firstname("test")
                .lastname("test")
                .gender(true)
                .phone("09824124512")
                .email("test@test.com")
                .dob(dob)
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void createUser_validRequest_success() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        // WHEN
        var response = userService.createUser(userCreationRequest);
        // THEN
        Assertions.assertThat(response.getUserId()).isEqualTo(Long.parseLong("12"));
        Assertions.assertThat(response.getUsername()).isEqualTo("test");
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void createUser_userExisted_fail() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        // WHEN
        var exception = Assert.assertThrows(AppException.class,
                () -> userService.createUser(userCreationRequest));
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode())
                .isEqualTo(1002);
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void getUserById_validId_success() {
        // GIVEN
        when(userRepository.findByUserIdAndDeletedFalse(any()))
                .thenReturn(java.util.Optional.of(user));
        // WHEN
        UserResponse response = userService.getUserById(12L);
        // THEN
        Assertions.assertThat(response.getUserId()).isEqualTo(12L);
        Assertions.assertThat(response.getUsername()).isEqualTo("test");
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void getUserById_invalidId_fail() {
        // GIVEN
        when(userRepository.findByUserIdAndDeletedFalse(any()))
                .thenReturn(java.util.Optional.empty());
        // WHEN
        AppException exception = Assert.assertThrows(AppException.class,
                () -> userService.getUserById(99L));
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1003); // Giả định mã lỗi
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void deleteUser_validId_success() {
        // GIVEN
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(user));
        // WHEN
        userService.deleteUser(12L);
        // THEN
        Assertions.assertThatNoException();
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void deleteUser_invalidId_fail() {
        // GIVEN
        when(userRepository.findById(any())).thenReturn(java.util.Optional.empty());
        // WHEN
        AppException exception = Assert.assertThrows(AppException.class,
                () -> userService.deleteUser(99L));
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1003); // Giả định mã lỗi
    }

    @Test
    @WithMockUser(username = "admin", roles = "DIRECTOR")
    void updateUser_duplicateEmail_fail() {
        // GIVEN
        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .firstname("newFirstname")
                .lastname("newLastname")
                .email("duplicate@test.com")
                .build();
        when(userRepository.findById(any())).thenReturn(java.util.Optional.of(user));
        when(userRepository.findByEmail(any()))
                .thenReturn(java.util.Optional.of(User.builder().userId(99L).build()));
        // WHEN
        AppException exception = Assert.assertThrows(AppException.class,
                () -> userService.updateUser(12L, updateRequest));
        // THEN
        Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1009); // Giả định mã lỗi
    }

}
