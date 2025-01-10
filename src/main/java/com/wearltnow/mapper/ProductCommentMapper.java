package com.wearltnow.mapper;

import com.wearltnow.dto.response.product.ProductCommentResponse;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductComment;
import com.wearltnow.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductCommentMapper {

    @Mapping(source = "id", target = "commentId")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "user", target = "userId", qualifiedByName = "mapUserToUserId")
    ProductCommentResponse toResponse(ProductComment productComment);

    // Phương thức ánh xạ tùy chỉnh để lấy ID từ đối tượng User
    @Named("mapUserToUserId")
    default Long mapUserToUserId(User user) {
        return user != null ? user.getUserId() : null;
    }

    // Kiểm tra null và xử lý ánh xạ
    @Named("mapProductToProductId")
    default Long mapProductToProductId(Product product) {
        return product != null ? product.getProductId() : null;
    }
}

