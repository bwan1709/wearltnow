package com.wearltnow.dto.response.product;

import com.wearltnow.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFavoriteResponse {
   ProductResponse product;
   UserResponse user;
}
