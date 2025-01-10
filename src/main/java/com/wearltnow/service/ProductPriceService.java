package com.wearltnow.service;

import com.wearltnow.dto.request.price.ProductPriceRequest;
import com.wearltnow.dto.response.price.ProductPriceResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.ProductPriceMapper;
import com.wearltnow.model.Price;
import com.wearltnow.model.Product;
import com.wearltnow.model.ProductPrice;
import com.wearltnow.repository.PriceRepository;
import com.wearltnow.repository.ProductPriceRepository;
import com.wearltnow.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductPriceService{
    ProductPriceRepository productPriceRepository;
    ProductPriceMapper productPriceMapper;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<ProductPriceResponse> findAll() {
        List<ProductPrice> productPrices = productPriceRepository.findAll();
        return productPrices.stream()
                .map(productPriceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<ProductPriceResponse> getAllByPriceId(Long priceId) {
        List<ProductPrice> productPrices = productPriceRepository.findByPriceId(priceId);
        if (productPrices.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_PRICE_NOT_FOUND);
        }
        return productPrices.stream()
                .map(productPriceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public ProductPriceResponse getById(Long id) {
        ProductPrice productPrice = productPriceRepository.findById(id)
                .orElseThrow(() ->new AppException(ErrorCode.PRODUCT_PRICE_NOT_FOUND));
        return productPriceMapper.toResponse(productPrice);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<ProductPriceResponse> create(List<ProductPriceRequest> productPriceRequests) {
        List<ProductPriceResponse> responses = new ArrayList<>();

        for (ProductPriceRequest productPriceDto : productPriceRequests) {
            // Lấy Product và Price từ cơ sở dữ liệu
            Product product = productRepository.findByProductIdAndDeletedFalse(productPriceDto.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

            Price price = priceRepository.findById(productPriceDto.getProductPriceId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

            // Tạo đối tượng ProductPrice và lưu vào cơ sở dữ liệu
            ProductPrice productPrice = productPriceMapper.toEntity(productPriceDto);
            productPrice.setProduct(product);
            productPrice.setPrice(price);

            ProductPrice savedPrice = productPriceRepository.save(productPrice);

            // Thêm ProductPriceResponse vào danh sách trả về
            responses.add(productPriceMapper.toResponse(savedPrice));
        }

        // Xóa bộ nhớ cache nếu cần thiết
        productService.deleteAllProductCache();

        return responses;
    }


    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public ProductPriceResponse update(Long id, ProductPriceRequest productPriceDto) {
        productRepository.findById(productPriceDto.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        priceRepository.findById(productPriceDto.getProductPriceId())
                .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

        // Xóa bộ nhớ cache nếu có
        productService.deleteAllProductCache();

        // Tìm ProductPrice hiện tại từ id
        ProductPrice existingPrice = productPriceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_PRICE_NOT_FOUND));

        // Cập nhật thông tin cho ProductPrice
        existingPrice.setPriceValue(productPriceDto.getPriceValue());

        // Lưu ProductPrice đã cập nhật vào cơ sở dữ liệu
        ProductPrice updatedPrice = productPriceRepository.save(existingPrice);

        // Trả về đối tượng ProductPriceResponse sau khi cập nhật
        return productPriceMapper.toResponse(updatedPrice);
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void delete(Long id) {
        productService.deleteAllProductCache();
        if (!productPriceRepository.existsById(id)) {
           throw new AppException(ErrorCode.PRODUCT_PRICE_NOT_FOUND);
        }
        productPriceRepository.deleteById(id);
    }
}
