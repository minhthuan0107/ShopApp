package com.project.shopapp.repositories;

import com.project.shopapp.models.Brand;
import com.project.shopapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {
    List<Brand> findByIsDeletedFalse();

    Optional<Brand> findByIdAndIsDeletedFalse(Long brandId);
    //Lấy thương hiệu của những sản phẩm thuộc danh mục
    @Query(value = "SELECT DISTINCT b.* FROM brands b " +
            "JOIN products p ON b.id = p.brand_id " +
            "WHERE p.category_id = :categoryId AND b.is_deleted = false ", nativeQuery = true)
    List<Brand> findDistinctByCategory(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT * FROM brands " +
            "WHERE REPLACE(LOWER(name), ' ', '') = REPLACE(LOWER(:name), ' ', '') " +
            "AND is_deleted = true LIMIT 1", nativeQuery = true)
    Optional<Brand> findByNameIgnoreCaseAndIsDeletedTrue(@Param("name") String name);

    @Query(value = "SELECT * FROM brands " +
            "WHERE REPLACE(LOWER(name), ' ', '') = REPLACE(LOWER(:name), ' ', '') " +
            "AND is_deleted = false LIMIT 1", nativeQuery = true)
    Optional<Brand> findByNameIgnoreCaseAndIsDeletedFalse(@Param("name") String name);
}
