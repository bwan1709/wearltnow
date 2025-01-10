package com.wearltnow.service;

import com.wearltnow.dto.request.product.ProductCommentRequest;
import com.wearltnow.dto.response.product.ProductCommentResponse;
import com.wearltnow.mapper.ProductCommentMapper;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductComment;
import com.wearltnow.model.User;
import com.wearltnow.repository.ProductCommentRepository;
import com.wearltnow.repository.ProductRepository;
import com.wearltnow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductCommentService {

    private final ProductCommentRepository productCommentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductCommentMapper productCommentMapper;
    private final DateTimeFormatter dateTimeFormatter;
    private final ProductService productService;

    public ProductCommentService(ProductCommentRepository productCommentRepository, ProductRepository productRepository,
                                 UserRepository userRepository, ProductCommentMapper productCommentMapper, DateTimeFormatter dateTimeFormatter, ProductService productService) {
        this.productCommentRepository = productCommentRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.productCommentMapper = productCommentMapper;
        this.dateTimeFormatter = dateTimeFormatter;
        this.productService = productService;
    }

    // Phương thức thiết lập parentId cho bình luận
    private void setParentIdForComment(ProductComment productComment, Long parentId) {
        if (parentId == null) {
            // Nếu không có parentId, đây là bình luận cha
            productComment.setParentId(null);
        } else {
            // Nếu có parentId, đây là bình luận con
            productComment.setParentId(parentId);
        }
    }

    // Tạo mới bình luận
    public ProductCommentResponse createComment(ProductCommentRequest productCommentRequest) {
        // Kiểm tra xem sản phẩm và người dùng có tồn tại không
        Product product = productRepository.findById(productCommentRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        User user = userRepository.findById(productCommentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo đối tượng ProductComment từ request
        ProductComment productComment = new ProductComment();
        productComment.setProduct(product);
        productComment.setUser(user);
        productComment.setContent(productCommentRequest.getContent());
        productComment.setRate(productCommentRequest.getRate());

        // Thiết lập parentId cho bình luận
        setParentIdForComment(productComment, productCommentRequest.getParentId());

        // Lưu vào cơ sở dữ liệu
        ProductComment savedProductComment = productCommentRepository.save(productComment);
        productService.deleteAllProductCache();
        return productCommentMapper.toResponse(savedProductComment);
    }

    // Lấy bình luận theo ID
    public ProductCommentResponse getCommentById(long id) {
        return productCommentRepository.findById(id)
                .map(productCommentMapper::toResponse)
                .orElse(null);
    }

    // Lấy tất cả các bình luận cho sản phẩm và xây dựng cấu trúc cây
    public List<ProductCommentResponse> getAllCommentsByProductId(long productId) {
        List<ProductComment> comments = productCommentRepository.findByProduct_ProductId(productId);
        List<ProductCommentResponse> responses = comments.stream()
                .map(productCommentMapper::toResponse)
                .collect(Collectors.toList());

        // Xây dựng cấu trúc cây cho các bình luận
        Map<Long, List<ProductCommentResponse>> commentMap = responses.stream()
                .collect(Collectors.groupingBy(ProductCommentResponse::getParentId));

        List<ProductCommentResponse> rootComments = commentMap.get(null);
        if (rootComments != null) {
            for (ProductCommentResponse parentComment : rootComments) {
                addChildrenToParent(parentComment, commentMap);
            }
        }

        return rootComments != null ? rootComments : List.of();
    }

    // Hàm đệ quy để thêm các bình luận con vào bình luận cha
    private void addChildrenToParent(ProductCommentResponse parent, Map<Long, List<ProductCommentResponse>> commentMap) {
        List<ProductCommentResponse> children = commentMap.get(parent.getCommentId());

        if (children != null) {
            // Set the children of the parent comment
            parent.setComments(children);

            for (ProductCommentResponse child : children) {
                // If createdAt is a LocalDateTime, convert it to Instant
                if (child.getCreatedAt() != null) {
                    // Convert LocalDateTime to Instant using the system's default timezone
                    Instant createdAtInstant = child.getCreatedAt()
                            .atZone(ZoneId.systemDefault()) // Convert to ZonedDateTime with system default timezone
                            .toInstant(); // Convert to Instant

                    // Format the Instant using the DateTimeFormatter
                    String formattedCreatedAt = dateTimeFormatter.format(createdAtInstant);

                    // Set the formatted createdAt string (not Instant) in the response
                    child.setCreated(formattedCreatedAt); // Set as formatted string, not Instant
                }

                // Recursively add children to parent
                addChildrenToParent(child, commentMap);
            }
        }
    }




    @Transactional(readOnly = true)
    public ProductCommentResponse getAllCommentsAndAverageRateByProductId(Long productId) {
        // Lấy danh sách các bình luận theo productId
        List<ProductComment> comments = productCommentRepository.findByProduct_ProductId(productId);

        // Chuyển các bình luận thành phản hồi ProductCommentResponse
        List<ProductCommentResponse> responses = comments.stream()
                .map(productCommentMapper::toResponse)
                .collect(Collectors.toList());

        // Tính toán trung bình đánh giá
        Double averageRate = productCommentRepository.findAverageRateByProductId(productId);

        // Xây dựng cấu trúc cây cho các bình luận
        Map<Long, List<ProductCommentResponse>> commentMap = responses.stream()
                .filter(response -> response.getParentId() != null) // Loại bỏ các bình luận có parentId null (nếu cần)
                .collect(Collectors.groupingBy(ProductCommentResponse::getParentId));

        // Lấy danh sách các bình luận cha (parentId == null)
        List<ProductCommentResponse> rootComments = responses.stream()
                .filter(response -> response.getParentId() == null) // Lọc ra các bình luận cha (parentId == null)
                .collect(Collectors.toList());

        // Nếu có bình luận cha, thêm các bình luận con vào
        if (rootComments != null) {
            for (ProductCommentResponse parentComment : rootComments) {
                addChildrenToParent(parentComment, commentMap);
            }
        }

        // Tạo đối tượng phản hồi bao gồm danh sách bình luận và trung bình đánh giá
        return new ProductCommentResponse(
                null, // Không cần commentId cho toàn bộ danh sách
                productId,
                null, // Không cần userId cho toàn bộ danh sách
                null, // Không cần content cho toàn bộ danh sách
                null, // Không cần rate cho toàn bộ danh sách
                null, // Không cần parentId cho toàn bộ danh sách
                null, // Không cần createdAt cho toàn bộ danh sách
                rootComments != null ? rootComments : List.of(), // Trả về danh sách bình luận cha nếu có
                averageRate != null ? averageRate : 0.0, // Trả về giá trị trung bình hoặc 0 nếu không có
                null
        );
    }


}

