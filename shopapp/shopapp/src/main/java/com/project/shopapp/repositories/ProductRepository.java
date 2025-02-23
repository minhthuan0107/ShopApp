package com.project.shopapp.repositories;

import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName (String name);
    Page<Product> findAll (Pageable pageable);//Phân trang
    List<Product> findByCategory(Category category);
    Page<Product> findByCategory (Category category,Pageable pageable);
}
