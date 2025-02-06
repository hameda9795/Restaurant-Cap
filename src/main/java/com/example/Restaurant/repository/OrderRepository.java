package com.example.Restaurant.repository;

import com.example.Restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Order entities.
 * Provides built-in CRUD operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);


}


//Why use OrderRepository.java?
//It handles database operations for Order without needing custom queries.
//Provides built-in methods like findAll(), findById(), save(), and delete().