package com.example.Restaurant.services;

import com.example.Restaurant.model.Recipe;
import com.example.Restaurant.model.RecipeIngredient;
import com.example.Restaurant.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    @Transactional
    public Recipe saveRecipe(Recipe recipe) {
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipe(recipe);
            }
        }
        return recipeRepository.save(recipe);
    }

    @Transactional
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public Optional<Recipe> getRecipeByFoodId(Long foodId) {
        return recipeRepository.findByFoodId(foodId);
    }
}