package com.example.Restaurant.services;

import com.example.Restaurant.model.*;
import com.example.Restaurant.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);

        // اگر سفارش به حالت Preparing می‌رود
        if ("Preparing".equals(status)) {
            if (!checkIngredientsAvailability(order)) {
                throw new RuntimeException("Insufficient ingredients for order #" + orderId);
            }
            updateIngredientsStock(order);
        }

        order.setStatus(status);
        if ("Delivered".equals(status)) {
            order.setDeliveryTime(LocalDateTime.now());
        }
        orderRepository.save(order);
    }

    private boolean checkIngredientsAvailability(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Recipe recipe = recipeService.getRecipeByFoodId(orderItem.getFood().getId())
                    .orElseThrow(() -> new RuntimeException("No recipe found for food: " + orderItem.getFood().getName()));

            for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
                Ingredient ingredient = recipeIngredient.getIngredient();
                double requiredAmount = recipeIngredient.getAmount() * orderItem.getQuantity();

                if (ingredient.getCurrentStock() < requiredAmount) {
                    // ارسال نوتیفیکیشن کمبود موجودی
                    messagingTemplate.convertAndSend("/topic/alerts",
                            Map.of(
                                    "type", "INSUFFICIENT_STOCK",
                                    "ingredient", ingredient.getName(),
                                    "required", requiredAmount,
                                    "available", ingredient.getCurrentStock()
                            ));
                    return false;
                }
            }
        }
        return true;
    }

    @Transactional
    private void updateIngredientsStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Recipe recipe = recipeService.getRecipeByFoodId(orderItem.getFood().getId())
                    .orElseThrow(() -> new RuntimeException("No recipe found for food: " + orderItem.getFood().getName()));

            for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
                Ingredient ingredient = recipeIngredient.getIngredient();
                double requiredAmount = recipeIngredient.getAmount() * orderItem.getQuantity();

                // به‌روزرسانی موجودی
                double newStock = ingredient.getCurrentStock() - requiredAmount;
                ingredient.setCurrentStock(newStock);

                // ذخیره تغییرات و ارسال به‌روزرسانی
                Ingredient updatedIngredient = ingredientService.saveIngredient(ingredient);

                // ارسال به‌روزرسانی real-time
                messagingTemplate.convertAndSend("/topic/stock-updates",
                        Map.of(
                                "type", "STOCK_UPDATE",
                                "ingredientId", updatedIngredient.getId(),
                                "newStock", updatedIngredient.getCurrentStock()
                        ));

                // چک کردن و ارسال هشدار اگر موجودی کم است
                if (newStock <= ingredient.getThreshold()) {
                    messagingTemplate.convertAndSend("/topic/alerts",
                            Map.of(
                                    "type", "LOW_STOCK_ALERT",
                                    "ingredient", ingredient
                            ));
                }
            }
        }
    }

    @Transactional
    public Order placeOrder(String tableNumber) {
        // Create new order
        Order order = new Order();
        order.setTableNumber(tableNumber);
        order.setOrderTime(LocalDateTime.now());
        order.setStatus("Pending");

        // Set other order properties as needed

        return orderRepository.save(order);
    }


    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
}