package com.project.shopapp.repositories;

import com.project.shopapp.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {
    @Query(value = "SELECT DISTINCT b.* FROM brands b JOIN products p ON b.id = p.brand_id WHERE p.category_id = :categoryId", nativeQuery = true)
    List<Brand> findDistinctByCategory(@Param("categoryId") Long categoryId);
}
