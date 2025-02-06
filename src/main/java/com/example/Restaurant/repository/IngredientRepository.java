// IngredientRepository.java
package com.example.Restaurant.repository;

import com.example.Restaurant.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
}