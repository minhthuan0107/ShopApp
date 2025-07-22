package com.project.shopapp.repositories;

import com.project.shopapp.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query(value = "SELECT * FROM comments c WHERE c.product_id = :productId " +
            "AND c.parent_id IS NULL ORDER BY c.create_at DESC",nativeQuery = true)
    List<Comment> findByProductIdAndParentCommentIsNull(Long productId);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.parentComment IS NULL " +
            "AND c.id NOT IN (SELECT r.comment.id FROM Rate r) " +
            "ORDER BY c.createAt DESC")
    Page<Comment> findAllParentCommentsExcludingRates(Pageable pageable);
    @Query("SELECT c FROM Comment c " +
            "WHERE c.parentComment IS NULL " +
            "AND c.id NOT IN (SELECT r.comment.id FROM Rate r) " +
            "AND (:keyword IS NULL OR LOWER(c.user.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.createAt DESC")
    Page<Comment> searchParentCommentsByUserFullName(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT c FROM Comment c " +
            "WHERE c.parentComment IS NULL " +
            "AND c.id IN (SELECT r.comment.id FROM Rate r) " +
            "ORDER BY c.createAt DESC")
    Page<Comment> findAllRatedParentComments(Pageable pageable);
    @Query("SELECT c FROM Comment c " +
            "WHERE c.parentComment IS NULL " +
            "AND c.id IN (SELECT r.comment.id FROM Rate r) " +
            "AND (:keyword IS NULL OR LOWER(c.user.fullname) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.createAt DESC")
    Page<Comment> searchRatedParentCommentsByUserFullName(@Param("keyword") String keyword, Pageable pageable);




}
