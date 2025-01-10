package com.wearltnow.mapper;

import com.wearltnow.dto.request.discount.DiscountCodeRequest;
import com.wearltnow.dto.response.discount.DiscountCodeResponse;
import com.wearltnow.model.DiscountCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DiscountCodeMapper {
    DiscountCodeMapper INSTANCE = Mappers.getMapper(DiscountCodeMapper.class);

    // Ánh xạ từ DiscountCodeRequest sang DiscountCode (Entity)
    @Mapping(target = "status", constant = "ACTIVE")  // Mặc định trạng thái là ACTIVE
    @Mapping(target = "userGroup", ignore = true)    // Nếu không cần ánh xạ group người dùng trực tiếp, có thể để nó null và xử lý sau
    DiscountCode toEntity(DiscountCodeRequest discountCodeRequest);
    @Mapping(target = "userGroupResponse",  source = "userGroup")
    DiscountCodeResponse toDTO(DiscountCode discountCode);
}
