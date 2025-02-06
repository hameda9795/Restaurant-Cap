package com.example.Restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping("/chef-dashboard")
    public String chefDashboard() {
        return "dashboard/chef-dashboard";
    }

    @GetMapping("/waiter-dashboard")
    public String waiterDashboard() {
        return "dashboard/waiter-dashboard";
    }

    @GetMapping("/souschef-dashboard")
    public String sousChefDashboard() {
        return "dashboard/souschef-dashboard";
    }
}