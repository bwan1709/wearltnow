package com.wearltnow.dto.request.user;

import com.wearltnow.validator.DobConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
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

    @DobConstraint(min = 18, message = "dob.invalid")
    @NotNull(message = "dob.notnull")
    LocalDate dob;

    List<String> roles;
}
