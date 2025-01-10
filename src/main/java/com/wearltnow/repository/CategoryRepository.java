package com.wearltnow.repository;

import com.wearltnow.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Page<Category> findAllByDeletedFalse(Specification<Category> specification,Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.slug LIKE %:slug%")
    Page<Category> findSlug(@Param("slug") String slug, Pageable pageable);
}
