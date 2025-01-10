package com.wearltnow.dto.request.user;

import com.wearltnow.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "username.invalid")
            @NotNull(message = "username.notnull")
    String username;

    @Size(min = 8, message = "password.invalid")
            @NotNull(message = "password.notnull")
    String password;

    @NotNull(message = "lastname.notnull")
    String lastname;

    @NotNull(message = "firstname.notnull")
    String firstname;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "phone.invalid")
            @NotNull(message = "phone.notnull")
    String phone;

    @Email(message = "email.invalid")
            @NotNull(message = "email.notnull")
    String email;

    @NotNull(message = "gender.notnull")
    Boolean gender;

    @DobConstraint(min = 10, message = "dob.invalid")
            @NotNull(message = "dob.notnull")
    LocalDate dob;
}
