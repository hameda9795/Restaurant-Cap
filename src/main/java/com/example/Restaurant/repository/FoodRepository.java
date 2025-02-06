// FoodRepository.java
package com.example.Restaurant.repository;

import com.example.Restaurant.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    // فقط برای نمایش غذاها در منو
    List<Food> findByCategory(String category);
}