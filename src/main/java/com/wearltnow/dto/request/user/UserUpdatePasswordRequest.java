package com.wearltnow.dto.request.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdatePasswordRequest {
    @NotNull(message = "password.notnull")
    @Size(min = 8, message = "password.invalid")
    String newPassword;
}
