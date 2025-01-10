package com.wearltnow.dto.response.auth;

import com.wearltnow.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    UserResponse user;
    String expires;
}
