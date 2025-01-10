package com.wearltnow.dto.response.user;

import com.wearltnow.dto.response.auth.RoleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long userId;
    String username;
    String lastname;
    String firstname;
    String phone;
    String email;
    Boolean gender;
    String address;
    LocalDate dob;

    Set<RoleResponse> roles;
}
