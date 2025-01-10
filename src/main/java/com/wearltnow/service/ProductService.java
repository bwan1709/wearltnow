package com.wearltnow.service;

import com.wearltnow.config.RedisConfig;
import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.product.ProductCreationRequest;
import com.wearltnow.dto.request.product.ProductUpdateRequest;
import com.wearltnow.dto.response.product.ProductResponse;
import com.wearltnow.dto.response.product.ProductSlugCategoryResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.ProductMapper;
import com.wearltnow.model.*;
import com.wearltnow.repository.*;
import com.wearltnow.service.base.BaseRedisServiceImpl;
import com.wearltnow.specification.ProductSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    CloudinaryService cloudinaryService;
    ProductMapper productMapper;
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductImageRepository productImageRepository;
    BaseRedisServiceImpl<String, String, PageResponse<ProductResponse>> redisService;
    RedisConfig redisConfig;
    RedisTemplate redisTemplate;
    @PersistenceContext
    EntityManager entityManager;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductCommentRepository productCommentRepository;
    private final ProductFavoriteRepository productFavoriteRepository;
    private final DiscountPriceRepository discountPriceRepository;

    public PageResponse<ProductResponse> findAllByDeletedFalse(
            int page, int size, String productName, String category,
            Double minPrice, Double maxPrice, LocalDate createdFrom, LocalDate createdTo,
            String productSize, String color, String sortBy, String sortDirection) {

        String cacheKey = buildCacheKey(page, size, productName, category, minPrice, maxPrice, createdFrom, createdTo,
                productSize, color, sortBy, sortDirection);

        PageResponse<ProductResponse> cachedResponse = redisService.get(cacheKey);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "productId";
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable;

        // Nếu sortBy là "favoriteCount", không sử dụng Pageable để phân trang
        if ("favoriteCount".equalsIgnoreCase(sortBy)) {
            // Chỉ phân trang mà không cần sắp xếp ở đây
            pageable = PageRequest.of(page - 1, size, Sort.by("productId"));  // Sử dụng "productId" mặc định
        } else {
            pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        }

        Specification<Product> specification = Specification.where(ProductSpecification.isNotDeleted());
        if (productName != null) specification = specification.and(ProductSpecification.hasName(productName));
        if (category != null) {
            try {
                Long categoryId = Long.parseLong(category);
                specification = specification.and(ProductSpecification.inCategory(categoryId));
            } catch (NumberFormatException ignored) {}
        }
        if (color != null) specification = specification.and(ProductSpecification.hasColor(color));
        if (productSize != null) specification = specification.and(ProductSpecification.hasSize(productSize));
        if (minPrice != null) specification = specification.and(ProductSpecification.priceGreaterThanOrEqualTo(minPrice));
        if (maxPrice != null) specification = specification.and(ProductSpecification.priceLessThanOrEqualTo(maxPrice));
        if (createdFrom != null && createdTo != null)
            specification = specification.and(ProductSpecification.createdDateBetween(createdFrom, createdTo));

        Page<Product> pageData = productRepository.findAll(specification, pageable);
        PageResponse<ProductResponse> response = PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(product -> {
                            ProductResponse productResponse = productMapper.toProductResponse(product);

                            // Lấy giá sản phẩm gốc
                            Double productPrice = getBasePrice(product.getProductId(),product);
                            // Lấy discountRate từ bảng DiscountPrice
                            Double discountRate = getDiscountRate(product.getProductId());

                            // Tính toán giá sau giảm nếu có discountRate
                            if (discountRate != null && discountRate > 0) {
                                // Tính giá sau giảm
                                Double discountedPrice = productPrice - (productPrice * discountRate / 100);
                                productResponse.setPrice(discountedPrice); // Cập nhật giá sau giảm
                                productResponse.setOriginalPrice(productPrice); // Cập nhật giá gốc
                                productResponse.setDiscountRate(discountRate); // Cập nhật tỷ lệ giảm giá

                            } else {
                                productResponse.setPrice(productPrice); // Nếu không có giảm giá, giữ nguyên giá gốc
                                productResponse.setOriginalPrice(productPrice); // Giá gốc
                                productResponse.setDiscountRate(0.0); // Không có giảm giá
                            }

                            // Thêm thông tin về đánh giá trung bình và số lượng yêu thích
                            productResponse.setAverageRate(productCommentRepository.findAverageRateByProductId(product.getProductId()));
                            productResponse.setFavoriteCount(productFavoriteRepository.countFavoritesByProductId(product.getProductId()));
                            return productResponse;
                        })
                        .toList())
                .build();

        if ("favoriteCount".equalsIgnoreCase(sortBy)) {
            List<ProductResponse> sortedData = response.getData().stream()
                    .sorted((p1, p2) -> {
                        if ("desc".equalsIgnoreCase(sortDirection)) {
                            return p2.getFavoriteCount().compareTo(p1.getFavoriteCount());
                        } else {
                            return p1.getFavoriteCount().compareTo(p2.getFavoriteCount());
                        }
                    })
                    .toList();
            response.setData(sortedData);
        }

        redisService.set(cacheKey, response);
        redisConfig.setWithDefaultTTL(redisTemplate, cacheKey, response);
        return response;
    }

    public PageResponse<ProductResponse> findDiscountedProducts(int page, int size, String sortBy, String sortDirection) {
        String cacheKey = "discountedProducts:page=" + page + ":size=" + size + ":sortBy=" + sortBy + ":sortDirection=" + sortDirection;

        PageResponse<ProductResponse> cachedResponse = redisService.get(cacheKey);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "productId";
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));

        // Lọc các sản phẩm có giảm giá
        Specification<Product> specification = Specification.where(ProductSpecification.isNotDeleted())
                .and(ProductSpecification.hasDiscount());

        Page<Product> pageData = productRepository.findAll(specification, pageable);
        PageResponse<ProductResponse> response = PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream()
                        .map(product -> {
                            ProductResponse productResponse = productMapper.toProductResponse(product);

                            // Lấy giá sản phẩm gốc
                            Double productPrice = getBasePrice(product.getProductId(),product);

                            // Lấy discountRate từ bảng DiscountPrice
                            Double discountRate = getDiscountRate(product.getProductId());

                            // Tính toán giá sau giảm nếu có discountRate
                            if (discountRate != null && discountRate > 0) {
                                Double discountedPrice = productPrice - (productPrice * discountRate / 100);
                                productResponse.setPrice(discountedPrice);
                                productResponse.setOriginalPrice(productPrice);
                                productResponse.setDiscountRate(discountRate);
                            } else {
                                productResponse.setPrice(productPrice);
                                productResponse.setOriginalPrice(productPrice);
                                productResponse.setDiscountRate(0.0);
                            }

                            // Thêm thông tin về đánh giá trung bình và số lượng yêu thích
                            productResponse.setAverageRate(productCommentRepository.findAverageRateByProductId(product.getProductId()));
                            productResponse.setFavoriteCount(productFavoriteRepository.countFavoritesByProductId(product.getProductId()));
                            return productResponse;
                        })
                        .toList())
                .build();

        redisService.set(cacheKey, response);
        redisConfig.setWithDefaultTTL(redisTemplate, cacheKey, response);
        return response;
    }

    private String buildCacheKey(int page, int size, String productName, String category,
                                 Double minPrice, Double maxPrice, LocalDate createdFrom, LocalDate createdTo,
                                 String productSize, String color, String sortBy, String sortDirection) {
        return String.format("products:page=%d:size=%d:name=%s:category=%s:minPrice=%s:maxPrice=%s:createdFrom=%s:createdTo=%s:size=%s:color=%s:sortBy=%s:sortDirection=%s",
                page, size,
                Optional.ofNullable(productName).orElse(""),
                Optional.ofNullable(category).orElse(""),
                Optional.ofNullable(minPrice).map(String::valueOf).orElse(""),
                Optional.ofNullable(maxPrice).map(String::valueOf).orElse(""),
                Optional.ofNullable(createdFrom).map(String::valueOf).orElse(""),
                Optional.ofNullable(createdTo).map(String::valueOf).orElse(""),
                Optional.ofNullable(productSize).orElse(""),
                Optional.ofNullable(color).orElse(""),
                Optional.ofNullable(sortBy).orElse(""),
                Optional.ofNullable(sortDirection).orElse(""));
    }

    public List<ProductSlugCategoryResponse> getProductsByCategorySlug(String slug){
        return productRepository.findByCategorySlug(slug).stream().map(product -> {
            ProductSlugCategoryResponse productResponse = productMapper.toProductSlugCategoryResponse(product);
            productResponse.setPrice(getProductPrice(product.getProductId()));
            return productResponse;
        }).toList();
    }
    public ProductResponse getOneProduct(Long id)  {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));
        ProductResponse response =  productMapper.toProductResponse(product);
        response.setPrice(getProductPrice(product.getProductId()));
        return response;
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
     public ProductResponse createProduct(ProductCreationRequest request) {
        // Chuyển đổi yêu cầu thành đối tượng Product
        Product product = productMapper.toProduct(request);

        // Kiểm tra nếu category không tồn tại
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));
        product.setCategory(category);

        // Upload ảnh chính
        MultipartFile imageFile = request.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                product.setImage(imageUrl);
            } catch (Exception e) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        } else {
            product.setImage(null);
        }

        // Lưu sản phẩm vào cơ sở dữ liệu để có ID cho các hình ảnh phụ
        product = productRepository.save(product);

        // Xử lý ảnh phụ nếu có
        List<MultipartFile> additionalImages = request.getAdditionalImages();
        List<ProductImage> newProductImages = new ArrayList<>();
        if (additionalImages != null && !additionalImages.isEmpty()) {
            for (MultipartFile additionalImage : additionalImages) {
                try {
                    String imageUrl = cloudinaryService.uploadImage(additionalImage);
                    ProductImage newProductImage = new ProductImage();
                    newProductImage.setImageUrl(imageUrl);
                    newProductImage.setProduct(product);
                    productImageRepository.save(newProductImage);
                    newProductImages.add(newProductImage);
                } catch (Exception e) {
                    System.out.println("Error uploading image: " + e.getMessage());
                    throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
                }
            }
        }
        product.setProductImages(newProductImages);

        // Lưu sản phẩm một lần nữa nếu cần thiết
        product = productRepository.save(product);

        // Construct the cache key based on the criteria for product listing
       deleteAllProductCache();
        return productMapper.toProductResponse(product);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findByProductIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        // Cập nhật các thông tin khác của sản phẩm từ request
        productMapper.updateProduct(product, request);

        // Xử lý ảnh chính nếu có
        MultipartFile newMainImage = request.getImage();
        if (newMainImage != null && !newMainImage.isEmpty()) {
            try {
                String newImageUrl = cloudinaryService.uploadImage(newMainImage);
                product.setImage(newImageUrl);
            } catch (Exception e) {
                throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        // Xử lý ảnh phụ nếu có
        List<MultipartFile> newAdditionalImages = request.getAdditionalImages();
        if (newAdditionalImages != null && !newAdditionalImages.isEmpty()) {
            // danh sách ảnh từ cơ sở dữ liệu
            List<ProductImage> currentImages = productImageRepository.findByProductId(product.getProductId());

            for (ProductImage currentImage : currentImages) {
                try {
                    productImageRepository.delete(currentImage);  // Xóa ảnh cũ
                } catch (Exception e) {
                    throw new AppException(ErrorCode.IMAGE_DELETE_FAILED);
                }
            }
            List<ProductImage> newProductImages = new ArrayList<>();
            for (MultipartFile imageFile : newAdditionalImages) {
                try {
                    String imageUrl = cloudinaryService.uploadImage(imageFile);
                    ProductImage newProductImage = new ProductImage();
                    newProductImage.setImageUrl(imageUrl);
                    newProductImage.setProduct(product);
                    productImageRepository.save(newProductImage);
                    newProductImages.add(newProductImage);
                } catch (Exception e) {
                    throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
                }
            }
            product.setProductImages(newProductImages);
        }

        product.setCategory(categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND)));

        // Lưu sản phẩm sau khi cập nhật
        product = productRepository.save(product);
        deleteAllProductCache();
        return productMapper.toProductResponse(product);
    }

    @CacheEvict(value = "products", allEntries = true)  // Xóa tất cả các cache liên quan đến "products"
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        deleteAllProductCache();
    }


    public PageResponse<ProductResponse> getTopSellingProducts(int page, int size, int limit) {
        // Lấy tất cả chi tiết đơn hàng
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();

        // Tính tổng số lượng bán của mỗi sản phẩm
        Map<Long, Integer> productSales = orderDetails.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getProduct().getProductId(),
                        Collectors.summingInt(OrderDetail::getQuantity)
                ));

        // Lấy danh sách các sản phẩm bán chạy theo số lượng
        List<Long> topSellingProductIds = productSales.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Lấy thông tin sản phẩm từ ProductRepository
        List<Product> products = productRepository.findAllById(topSellingProductIds);

        // Chuyển đổi từ Product sang ProductResponse sử dụng ProductMapper
        List<ProductResponse> productResponses = products.stream()
                .map(product -> {
                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    productResponse.setPrice(getProductPrice(product.getProductId()));
                    return productResponse;
                })
                .collect(Collectors.toList());

        // Tạo đối tượng PageResponse để trả về
        return PageResponse.<ProductResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(1)  // Vì ở đây chỉ lấy top-selling products, không phân trang
                .totalElements(productResponses.size())  // Tổng số sản phẩm
                .data(productResponses)
                .build();
    }


    public void deleteAllProductCache() {
        try (Jedis jedis = new Jedis("47.245.91.25", 6379)) {
            // Kết nối Redis
            String cursor = "0";  // Bắt đầu từ con trỏ ban đầu
            do {
                // Quét các key với pattern "products*"
                ScanResult<String> scanResult = jedis.scan(cursor, new ScanParams().match("\"products:*\"").count(1000));

                // In ra kết quả của key

                // Xóa từng key
                for (String key : scanResult.getResult()) {
                    jedis.del(key);
                }

                cursor = scanResult.getCursor();  // Tiến hành quét tiếp với con trỏ mới
            } while (!cursor.equals("0"));  // Dừng khi con trỏ quay lại "0"

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        // Đảm bảo đóng kết nối
    }
    public Double getProductPrice(Long productId) {
        // Tìm sản phẩm từ bảng products
        Product product = entityManager.find(Product.class, productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        // Kiểm tra xem sản phẩm có giảm giá không
        String discountQuery = "SELECT dp FROM DiscountPrice dp " +
                "WHERE dp.product.id = :productId " +
                "AND dp.startDate <= :now " +
                "AND dp.endDate >= :now";
        List<DiscountPrice> discountPrices = entityManager.createQuery(discountQuery, DiscountPrice.class)
                .setParameter("productId", productId)
                .setParameter("now", LocalDate.now())
                .getResultList();

        if (!discountPrices.isEmpty()) {
            DiscountPrice discount = discountPrices.get(0);

            // Lấy giá từ bảng giá hoặc giá sản phẩm
            Double basePrice = getBasePrice(productId, product);
            return basePrice * (1 - discount.getDiscountRate() / 100);
        }
        // Nếu không có giảm giá, trả về giá từ bảng giá hoặc giá sản phẩm
        return getBasePrice(productId, product);
    }


    private Double getBasePrice(Long productId, Product product) {
        // Kiểm tra bảng ProductPrice
        String query = "SELECT pp FROM ProductPrice pp " +
                "JOIN pp.price p " +
                "WHERE pp.product.id = :productId " +
                "AND p.startDate <= :now " +
                "AND p.endDate >= :now";
        List<ProductPrice> productPrices = entityManager.createQuery(query, ProductPrice.class)
                .setParameter("productId", productId)
                .setParameter("now", LocalDate.now())
                .getResultList();

        if (!productPrices.isEmpty()) {
            return productPrices.get(0).getPriceValue();
        }

        // Trả về giá mặc định nếu không có giá trong bảng giá
        return product.getPrice() != null ? product.getPrice() : 0.0;
    }
    private Double getDiscountRate(Long productId) {
        // Tìm sản phẩm theo productId
        Product product = productRepository.findById(productId).orElse(null);

        // Nếu không tìm thấy sản phẩm, trả về null
        if (product == null) {
            return null;
        }
        // Tìm DiscountPrice theo sản phẩm và kiểm tra xem có bị xóa hay không
        Optional<DiscountPrice> discountPrice = discountPriceRepository.findByProductAndDeletedFalse(product);

        // Nếu không có discountPrice, trả về null
        return discountPrice.map(DiscountPrice::getDiscountRate).orElse(null);
    }
}

