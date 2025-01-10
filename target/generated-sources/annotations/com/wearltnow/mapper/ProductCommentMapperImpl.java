package com.wearltnow.mapper;

import com.wearltnow.dto.response.product.ProductCommentResponse;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductComment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-10T19:38:15+0700",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProductCommentMapperImpl implements ProductCommentMapper {

    @Override
    public ProductCommentResponse toResponse(ProductComment productComment) {
        if ( productComment == null ) {
            return null;
        }

        ProductCommentResponse.ProductCommentResponseBuilder productCommentResponse = ProductCommentResponse.builder();

        productCommentResponse.commentId( productComment.getId() );
        productCommentResponse.productId( productCommentProductProductId( productComment ) );
        productCommentResponse.userId( mapUserToUserId( productComment.getUser() ) );
        productCommentResponse.content( productComment.getContent() );
        productCommentResponse.rate( productComment.getRate() );
        productCommentResponse.parentId( productComment.getParentId() );
        productCommentResponse.createdAt( productComment.getCreatedAt() );

        return productCommentResponse.build();
    }

    private Long productCommentProductProductId(ProductComment productComment) {
        if ( productComment == null ) {
            return null;
        }
        Product product = productComment.getProduct();
        if ( product == null ) {
            return null;
        }
        Long productId = product.getProductId();
        if ( productId == null ) {
            return null;
        }
        return productId;
    }
}
