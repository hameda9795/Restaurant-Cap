package com.example.Restaurant.model;

import jakarta.persistence.*;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double currentStock;
    private Double threshold;

    @Transient // این فیلد در دیتابیس ذخیره نمیشه
    public String getStockStatus() {
        if (currentStock <= 0) {
            return "OUT_OF_STOCK";
        } else if (currentStock <= threshold) {
            return "LOW_STOCK";
        } else if (currentStock <= threshold * 2) {
            return "MEDIUM_STOCK";
        } else {
            return "WELL_STOCKED";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Double currentStock) {
        this.currentStock = currentStock;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }


}