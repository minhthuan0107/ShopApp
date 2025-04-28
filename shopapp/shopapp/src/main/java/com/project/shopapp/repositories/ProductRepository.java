package com.project.shopapp.repositories;

import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.projection.PriceRangeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName (String name);
    Page<Product> findAll (Pageable pageable);//Phân trang
    List<Product> findByCategory(Category category);
    @Query("SELECT p FROM Product p " +
            "WHERE p.category.id = :categoryId " +
            "AND (:brandIds IS NULL OR p.brand.id IN :brandIds) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findByFilters(
            @Param("categoryId") Long categoryId,
            @Param("brandIds") List<Long> brandIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :likePattern, '%')) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchByKeyword(@Param("likePattern") String likePattern,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  Pageable pageable);

    @Query(value = "SELECT * FROM products p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :likePattern, '%')) LIMIT 3", nativeQuery = true)
    List<Product> getProductSuggestions(@Param("likePattern") String likePattern);

    @Query(value = "SELECT MIN(price) AS minPrice, MAX(price) AS maxPrice FROM products", nativeQuery = true)
    PriceRangeProjection findMinAndMaxPrice();
}
