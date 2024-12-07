package com.project.shopapp.repositories;

import com.project.shopapp.models.Order;
import com.project.shopapp.response.OrderResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserId (Long userId);


}
