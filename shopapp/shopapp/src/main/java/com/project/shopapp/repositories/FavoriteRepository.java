package com.project.shopapp.repositories;
import com.project.shopapp.models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndProductId(Long userId,Long productId);

    List<Favorite> findByUserId( Long userId);

    void deleteByUserId(Long userId);
    @Query(value = "SELECT COUNT(*) FROM favorites WHERE user_id = :userId", nativeQuery = true)
    Long countFavoriteItems (@Param("userId") Long userId);
}
