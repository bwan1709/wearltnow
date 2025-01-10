package com.wearltnow.service;

import com.wearltnow.dto.request.price.PriceRequest;
import com.wearltnow.dto.response.price.PriceResponse;
import com.wearltnow.exception.AppException;
import com.wearltnow.exception.ErrorCode;
import com.wearltnow.mapper.PriceMapper;
import com.wearltnow.model.Price;
import com.wearltnow.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;
    private final PriceMapper priceMapper;
    private final ProductService productService;

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public List<PriceResponse> findAll() {
        return priceRepository.findAll().stream()
                .map(priceMapper::toResponse)
                .collect(Collectors.toList());
    }
    // Lấy chi tiết Price theo ID
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public PriceResponse getById(Long id) {
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));
        return priceMapper.toResponse(price);
    }

    // Tạo mới Price
    @Transactional
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public PriceResponse create(PriceRequest priceRequestDto) {
        productService.deleteAllProductCache();
        Price price = priceMapper.toEntity(priceRequestDto);
        Price savedPrice = priceRepository.save(price);
        return priceMapper.toResponse(savedPrice);
    }

    @Transactional
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public PriceResponse update(Long id, PriceRequest priceRequestDto) {
        productService.deleteAllProductCache();
        Price existingPrice = priceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

        existingPrice.setName(priceRequestDto.getName());
        existingPrice.setStartDate(priceRequestDto.getStartDate());
        existingPrice.setEndDate(priceRequestDto.getEndDate());

        Price updatedPrice = priceRepository.save(existingPrice);
        return priceMapper.toResponse(updatedPrice);
    }

    // Xóa Price theo ID
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('STAFF')")
    public void delete(Long id) {
        productService.deleteAllProductCache();
        if (!priceRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRICE_NOT_FOUND);
        }
        priceRepository.deleteById(id);
    }
}
