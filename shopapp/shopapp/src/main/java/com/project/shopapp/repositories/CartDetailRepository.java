package com.project.shopapp.repositories;

import com.project.shopapp.models.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

    Optional<CartDetail> findByCartIdAndProductId(Long cartId, Long productId);

    List<CartDetail> findByCartId (Long cartId);

    @Query(value ="SELECT COALESCE(SUM(cd.quantity), 0) FROM cart_details cd WHERE cd.cart_id = :cartId", nativeQuery = true)
    int sumQuantityByCartId(@Param("cartId") Long cartId);

    @Query(value = "SELECT COALESCE(SUM(cd.total_price), 0) FROM cart_details cd WHERE cd.cart_id = :cartId", nativeQuery = true)
    BigDecimal sumTotalPriceByCartId(@Param("cartId") Long cartId);
    @Query(value = "SELECT COUNT(*) FROM cart_details WHERE cart_id = :cartId", nativeQuery = true)
    Long countCartItems(@Param("cartId") Long cartId);
    void deleteByCartId (Long cartId);
    void deleteById(Long cartDetailId);
}
