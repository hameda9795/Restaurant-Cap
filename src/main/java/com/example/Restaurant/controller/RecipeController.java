package com.example.Restaurant.controller;

import com.example.Restaurant.model.Recipe;
import com.example.Restaurant.services.RecipeService;
import com.example.Restaurant.services.FoodService;
import com.example.Restaurant.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recipe-management")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private FoodService foodService;

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public String viewRecipes(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        model.addAttribute("foods", foodService.getAllFoods());
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        return "recipe-management";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveRecipe(@RequestBody Recipe recipe) {
        try {
            Recipe savedRecipe = recipeService.saveRecipe(recipe);
            return ResponseEntity.ok(savedRecipe);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getRecipe(@PathVariable Long id) {
        return recipeService.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}