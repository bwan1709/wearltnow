package com.wearltnow.dto.response.notify;

import com.wearltnow.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    private Long id;

    private String message;

    private String type;

    private String status;

    private UserResponse user;
}
