package com.project.shopapp.repositories;
import com.project.shopapp.models.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate,Long> {
    Optional<Rate> findByCommentId(Long commentId);
    List<Rate> findByProductId(Long productId);
    @Query(value = "SELECT r.product_id,COUNT(*),AVG(r.rating) FROM rates r WHERE r.product_id IN (:productIds) " +
            "GROUP BY r.product_id",nativeQuery = true)
    List<Object[]> findStatsByProductIds(@Param("productIds") List<Long> productIds);

    @Query(value = "SELECT COUNT(*),AVG(r.rating) FROM rates r WHERE r.product_id = :productId ",nativeQuery = true)
    Object findStatByProductId(@Param("productId") Long productId);
}
