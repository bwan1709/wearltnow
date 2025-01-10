package com.wearltnow.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotBlank(message = "username.notnull")
    String username;

    @NotBlank(message = "password.notnull")
    @Size(min = 8, message = "password.invalid")
    String password;
    @Email(message = "email.invalid")
            @NotBlank(message = "email.notnull")
    String email;
}
