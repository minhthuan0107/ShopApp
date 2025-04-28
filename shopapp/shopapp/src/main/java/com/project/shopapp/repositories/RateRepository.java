package com.project.shopapp.repositories;
import com.project.shopapp.models.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate,Long> {
    Optional<Rate> findByCommentId(Long commentId);
    List<Rate> findByProductId(Long productId);
}
