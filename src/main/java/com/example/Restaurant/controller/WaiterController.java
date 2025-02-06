package com.example.Restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/waiters")
public class WaiterController {

    @GetMapping
    public String viewWaiterDashboard() {
        return "waiters"; // این به waiters.html اشاره می‌کند
    }
}