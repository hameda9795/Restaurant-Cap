package com.example.Restaurant.controller;

import com.example.Restaurant.model.Ingredient;
import com.example.Restaurant.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public String viewIngredients(Model model) {
        List<Ingredient> allIngredients = ingredientService.getAllIngredients();
        List<Ingredient> lowStockIngredients = ingredientService.getLowStockIngredients();

        model.addAttribute("ingredients", allIngredients);
        model.addAttribute("lowStockIngredients", lowStockIngredients);
        model.addAttribute("totalCount", allIngredients.size());
        model.addAttribute("outOfStockCount", ingredientService.getOutOfStockCount());
        model.addAttribute("lowStockCount", lowStockIngredients.size());
        model.addAttribute("wellStockedCount", ingredientService.getWellStockedCount());

        return "ingredients";
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public ResponseEntity<Ingredient> getIngredient(@PathVariable Long id) {
        return ingredientService.getIngredientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Ingredient> addIngredient(@RequestBody Ingredient ingredient) {
        Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
        checkAndNotifyStockLevel(savedIngredient);
        return ResponseEntity.ok(savedIngredient);
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredient) {
        return ingredientService.getIngredientById(id)
                .map(existing -> {
                    ingredient.setId(id);
                    Ingredient updated = ingredientService.saveIngredient(ingredient);
                    checkAndNotifyStockLevel(updated);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/update-stock/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        return ingredientService.getIngredientById(id)
                .map(ingredient -> {
                    ingredient.setCurrentStock(request.get("currentStock"));
                    Ingredient updated = ingredientService.saveIngredient(ingredient);
                    checkAndNotifyStockLevel(updated);

                    // Send real-time update
                    messagingTemplate.convertAndSend("/topic/stock-updates",
                            Map.of(
                                    "type", "STOCK_UPDATE",
                                    "ingredientId", updated.getId(),
                                    "newStock", updated.getCurrentStock()
                            ));

                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteIngredient(@PathVariable Long id) {
        if (ingredientService.getIngredientById(id).isPresent()) {
            ingredientService.deleteIngredient(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private void checkAndNotifyStockLevel(Ingredient ingredient) {
        if (ingredient.getCurrentStock() <= ingredient.getThreshold()) {
            messagingTemplate.convertAndSend("/topic/alerts",
                    Map.of(
                            "type", "LOW_STOCK_ALERT",
                            "ingredient", ingredient
                    ));
        }
    }
}