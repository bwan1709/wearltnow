package com.wearltnow.service;

import com.wearltnow.dto.PageResponse;
import com.wearltnow.dto.request.discount_price.DiscountPriceRequest;
import com.wearltnow.dto.response.discount_price.DiscountPriceResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.DiscountPriceMapper;
import com.wearltnow.model.DiscountPrice;
import com.wearltnow.model.Product;
import com.wearltnow.repository.DiscountPriceRepository;
import com.wearltnow.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscountPriceService {
    private final DiscountPriceRepository discountPriceRepository;
    private final DiscountPriceMapper discountPriceMapper;
    private final ProductRepository productRepository;
    private final ProductService productService;


    // Lấy tất cả DiscountPrice
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public PageResponse<DiscountPriceResponse> getAllDiscountPrices(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = discountPriceRepository.findAllByDeletedFalse(pageable);
        return PageResponse.<DiscountPriceResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(discountPriceMapper::toResponse).toList())
                .build();
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public DiscountPriceResponse getDiscountPriceById(Long id)
    {
        return discountPriceMapper.toResponse(discountPriceRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND)));
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public DiscountPriceResponse create(DiscountPriceRequest request) throws Exception {
            productRepository.findByProductIdAndDeletedFalse(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));
            DiscountPrice discountPrice = discountPriceMapper.toEntity(request);
            discountPriceRepository.save(discountPrice);
            productService.deleteAllProductCache();
            return discountPriceMapper.toResponse(discountPrice);
    }

    // Cập nhật DiscountPrice
    @Transactional
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public DiscountPriceResponse update(Long id, DiscountPriceRequest request) throws Exception {
            DiscountPrice existingDiscountPrice = discountPriceRepository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_PRICE_NOT_FOUND));

            // Cập nhật các thuộc tính cần thiết
            Product product = productRepository.findByProductIdAndDeletedFalse(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));
            discountPriceMapper.updateDiscountPrice(existingDiscountPrice, request);

            // Cập nhật sản phẩm vào DiscountPrice
            existingDiscountPrice.setProduct(product);
        productService.deleteAllProductCache();
            // Lưu lại DiscountPrice đã cập nhật
            return discountPriceMapper.toResponse(discountPriceRepository.save(existingDiscountPrice));
    }

    @Transactional
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void delete(Long id) {
        DiscountPrice discountPrice = discountPriceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_PRICE_NOT_FOUND));
        productService.deleteAllProductCache();
        discountPriceRepository.delete(discountPrice);
    }
}
