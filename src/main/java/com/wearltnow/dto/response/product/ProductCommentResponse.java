package com.wearltnow.dto.response.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCommentResponse {
    private Long commentId;           // ID của bình luận
    private Long productId;           // ID sản phẩm
    private Long userId;              // ID người dùng
    private String content;            // Nội dung bình luận
    private Integer rate;              // Đánh giá sao (1-5)
    private Long parentId;             // ID bình luận cha (nếu có)
    private LocalDateTime createdAt;
    // Thêm thuộc tính để chứa danh sách bình luận và trung bình đánh giá
    private List<ProductCommentResponse> comments; // Danh sách bình luận
    private Double averageRate; // Trung bình đánh giá
    private String created;
}
