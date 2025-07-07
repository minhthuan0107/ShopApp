package com.project.shopapp.repositories;

import com.project.shopapp.models.Category;
import com.project.shopapp.models.Order;
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
    Page<Product> findAllByIsDeletedFalse(Pageable pageable);
    List<Product> findByCategoryAndIsDeletedFalse(Category category);
    @Query("SELECT p FROM Product p " +
            "WHERE p.isDeleted = false " +  // ✅ Lọc sản phẩm chưa bị xóa mềm
            "AND p.category.id = :categoryId " +
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
    //Truy vấn danh sách product khi user gõ keyword
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false " +
            "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :likePattern, '%')) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchByKeyword(@Param("likePattern") String likePattern,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  Pageable pageable);
   //Truy vấn 3 gợi ý 3 product khi user gõ keyword
   @Query(value = "SELECT * FROM products p " +
           "WHERE p.is_deleted = false " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :likePattern, '%')) " +
           "LIMIT 3", nativeQuery = true)
   List<Product> getProductSuggestions(@Param("likePattern") String likePattern);

    @Query(value = "SELECT * FROM products p WHERE p.is_deleted = false " +
            "ORDER BY sold DESC LIMIT 14",nativeQuery = true)
    List<Product> findTop14BestSellingProducts();
    //Truy vấn 14 product đc đánh giá cao nhất
    @Query(value = "SELECT p.*, COUNT(r.id) AS total_reviews, AVG(r.rating) as avg_rating " +
            "FROM products p " +
            "LEFT JOIN rates r ON r.product_id = p.id " +
            "WHERE p.is_deleted = false " +
            "GROUP BY p.id " +
            "ORDER BY (avg_rating IS NULL), avg_rating DESC, total_reviews DESC " +
            "LIMIT 14", nativeQuery = true)
    List<Product> findTop14MostHighlyRatedProducts();
    //Truy vấn giá trị min hoặc max (price) của product
    @Query(value = "SELECT MIN(price) AS minPrice, MAX(price) AS maxPrice FROM products " +
            " WHERE is_deleted = false", nativeQuery = true)
    PriceRangeProjection findMinAndMaxPrice();

    //Truy vấn danh sách product theo keyword(Admin)
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR str(p.id) LIKE CONCAT('%', :keyword, '%'))")
    Page<Product> searchProductsByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
