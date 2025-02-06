package com.example.Restaurant.model;

import jakarta.persistence.*;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Food food;

    // private Integer tableNumber (thymeleaf and create form in
    // shopping cart the position is onder the menu page)

    private Integer quantity;

    private Double totalPrice;

    public CartItem() {
        this.quantity = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if(food != null) {
            this.totalPrice = food.getPrice() * quantity;
        }
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }


}

//Why use CartItem?
//This model represents items in a shopping cart.
//It tracks the food item, quantity, and total price.