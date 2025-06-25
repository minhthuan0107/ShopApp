package com.project.shopapp.repositories;

import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User>  findByPhoneNumber (String phoneNumber);

    Optional<User>  findByGoogleAccountId (String googleAccountId);

    @Query("SELECT u FROM User u WHERE u.role.name <> 'ADMIN'")
    Page<User> findAllExcludeAdmin(Pageable pageable);
    @Query("SELECT u FROM User u WHERE (LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND u.role.name <> 'ADMIN'")
    Page<User> searchUsersByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
