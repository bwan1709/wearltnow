package com.wearltnow.service;
import com.wearltnow.dto.response.product.ProductFavoriteResponse;
import com.wearltnow.mapper.ProductFavoriteMapper;
import com.wearltnow.model.ProductFavorite;
import com.wearltnow.model.User;
import com.wearltnow.model.Product;
import com.wearltnow.repository.ProductFavoriteRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductFavoriteService {
    ProductFavoriteRepository productFavoriteRepository;
    private final ProductFavoriteMapper productFavoriteMapper;

    public ProductFavorite addFavorite(User user, Product product) {
        Optional<ProductFavorite> existingFavorite = productFavoriteRepository.findByUserAndProduct(user, product);
        if (existingFavorite.isPresent()) {
            return existingFavorite.get();
        }
        ProductFavorite favorite = new ProductFavorite(user, product);
        return productFavoriteRepository.save(favorite);
    }

    public List<ProductFavoriteResponse> getFavoritesByUser(User user) {
        return productFavoriteRepository.findByUser(user).stream().map(productFavoriteMapper::toResponse).toList();
    }
    @Transactional
    public void removeFavorite(User user, Product product) {
        productFavoriteRepository.deleteByUserAndProduct(user, product);
    }

    public boolean isFavorite(User user, Product product) {
        return productFavoriteRepository.findByUserAndProduct(user, product).isPresent();
    }
}