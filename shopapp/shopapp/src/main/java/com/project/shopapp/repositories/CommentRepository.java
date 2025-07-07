package com.project.shopapp.repositories;

import com.project.shopapp.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query(value = "SELECT * FROM comments c WHERE c.product_id = :productId AND c.parent_id IS NULL ORDER BY c.create_at DESC",nativeQuery = true)
    List<Comment> findByProductIdAndParentCommentIsNull(Long productId);



}
