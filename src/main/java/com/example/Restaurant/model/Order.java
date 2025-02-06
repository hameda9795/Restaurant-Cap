package com.example.Restaurant.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an order placed by a customer.
 * Stores table number, order time, items, total price, and status.
 */
@Entity
@Table(name = "customer_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Table number (1-25)
    private String tableNumber;

    // Time when the order was placed
    private LocalDateTime orderTime;

    // List of items in the order
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    // Total price of the order
    private Double totalPrice;

    // Order status (Pending, Preparing, Ready, Delivered)
    private String status;

    // Time when the order was delivered
    private LocalDateTime deliveryTime;

    // Default constructor
    public Order() {
        this.orderTime = LocalDateTime.now();
        this.status = "Pending";
    }

    // Full constructor
    public Order(String tableNumber, List<OrderItem> orderItems, Double totalPrice) {
        this();
        this.tableNumber = tableNumber;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    // Helper method to calculate total price based on order items
    public void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();
    }

    // Helper method to add an item to the order
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        calculateTotalPrice();
    }

    // Helper method to remove an item from the order
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        calculateTotalPrice();
    }
}