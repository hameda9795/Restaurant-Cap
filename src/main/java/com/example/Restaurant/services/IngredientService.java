package com.example.Restaurant.services;

import com.example.Restaurant.model.Ingredient;
import com.example.Restaurant.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    @Transactional
    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    public List<Ingredient> getLowStockIngredients() {
        return getAllIngredients().stream()
                .filter(i -> i.getCurrentStock() <= i.getThreshold())
                .collect(Collectors.toList());
    }

    public long getOutOfStockCount() {
        return getAllIngredients().stream()
                .filter(i -> i.getCurrentStock() <= 0)
                .count();
    }

    public long getWellStockedCount() {
        return getAllIngredients().stream()
                .filter(i -> i.getCurrentStock() > i.getThreshold())
                .count();
    }

    @Transactional
    public void updateStock(Long id, Double newStock) {
        ingredientRepository.findById(id).ifPresent(ingredient -> {
            ingredient.setCurrentStock(newStock);
            ingredientRepository.save(ingredient);
        });
    }
}