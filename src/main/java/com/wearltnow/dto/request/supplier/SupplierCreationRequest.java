package com.wearltnow.dto.request.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierCreationRequest {
    private String description;

    @Email(message = "company-email.invalid")
    @NotNull(message = "company-email.notnull")
    private String email;

    @NotNull(message = "company-contact-person.notnull")
    private String contactPerson;

    @NotNull(message = "company-name.notnull")
    private String name;

    @Pattern(regexp = "^(http://|https://)?([\\w-]+\\.)+[\\w-]+(/([\\w-]+)*)?$", message = "company-website.invalid")
    private String website;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "company-phone.invalid")
    private String phone;

    @Pattern(regexp = "^[0-9]{10}$", message = "company-tax-code.invalid")
    private String taxCode;
}
