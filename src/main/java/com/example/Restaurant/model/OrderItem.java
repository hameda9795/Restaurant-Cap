package com.example.Restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

/**
 * Represents a food item in an order.
 * Links an `Order` to a `Food` item with quantity and price details.
 */
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    @ManyToOne
    private Food food; // Each order item represents a food item

    private Integer quantity; // Quantity of the food item in the order
    private Double price; // Price of the food item (food price * quantity)

    public OrderItem() {}

    public OrderItem(Order order, Food food, Integer quantity) {
        this.order = order;
        this.food = food;
        this.quantity = quantity;
        this.price = food.getPrice() * quantity;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}


//Why use OrderItem.java?
//It stores each item in a placed order.
//Keeps track of food item details, quantity, and price.
//Helps in managing and displaying order history.
