package com.example.Restaurant.config;

import com.example.Restaurant.model.Role;
import com.example.Restaurant.model.User;
import com.example.Restaurant.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setRole(Role.ADMIN);
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setIsActive(true);
            userService.createUser(admin);

            System.out.println("Admin user created successfully!");
        }
    }
}