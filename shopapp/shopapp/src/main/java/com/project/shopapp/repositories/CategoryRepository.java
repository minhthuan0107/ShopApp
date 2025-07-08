package com.project.shopapp.repositories;

import com.project.shopapp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByIsDeletedFalse();

    Optional<Category> findByIdAndIsDeletedFalse(Long categoryId);

    @Query(value = "SELECT * FROM categories " +
            "WHERE REPLACE(LOWER(name), ' ', '') = REPLACE(LOWER(:name), ' ', '') " +
            "AND is_deleted = true LIMIT 1", nativeQuery = true)
    Optional<Category> findByNameIgnoreCaseAndIsDeletedTrue(@Param("name") String name);

    @Query(value = "SELECT * FROM categories " +
            "WHERE REPLACE(LOWER(name), ' ', '') = REPLACE(LOWER(:name), ' ', '') " +
            "AND is_deleted = false LIMIT 1", nativeQuery = true)
    Optional<Category> findByNameIgnoreCaseAndIsDeletedFalse(@Param("name") String name);

}
