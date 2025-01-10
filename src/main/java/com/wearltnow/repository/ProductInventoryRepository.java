package com.wearltnow.repository;

import com.wearltnow.model.Product;
import com.wearltnow.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository  extends JpaRepository<ProductInventory, Long> {
    Optional<ProductInventory> findByProductAndColorAndSize(Product product, String color, String size);
    List<ProductInventory> findByProduct(Product product);
    List<ProductInventory> findByQuantityLessThan(int quantity);
}
