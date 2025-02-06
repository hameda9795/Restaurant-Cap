package com.example.Restaurant.controller;

import com.example.Restaurant.model.Food;
import com.example.Restaurant.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class FoodController {
    @Autowired
    private FoodRepository foodRepository;

    // Home Page
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // Menu-Category
    @GetMapping("/menu/{category}")
    public String menu(@PathVariable String category, Model model) {
        model.addAttribute("foods", foodRepository.findByCategory(category));
        return "menu";
    }

    // Manage Food
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("foods", foodRepository.findAll());
        return "form";
    }

    // Form
    @GetMapping("/form/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food Id:" + id));
        model.addAttribute("food", food);
        model.addAttribute("isEdit", true);
        return "form";
    }

    // Delete
    @PostMapping("/form/delete/{id}")
    public String deleteFood(@PathVariable Long id) {
        foodRepository.deleteById(id);
        return "redirect:/form";
    }

    // Update
    @PostMapping("/save")
    public String saveFood(@ModelAttribute Food food, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                Files.createDirectories(Paths.get(uploadDir));
                Files.copy(file.getInputStream(),
                        Paths.get(uploadDir + fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                food.setImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        foodRepository.save(food);
        return "redirect:/form";
    }

    @GetMapping("/api/foods/{id}")
    @ResponseBody
    public Food getFoodById(@PathVariable Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food Id:" + id));
    }
}