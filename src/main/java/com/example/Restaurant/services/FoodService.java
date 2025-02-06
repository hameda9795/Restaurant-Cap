package com.example.Restaurant.services;

import com.example.Restaurant.model.Food;
import com.example.Restaurant.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    // دریافت تمام غذاها
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    // دریافت غذاها بر اساس دسته‌بندی
    public List<Food> getFoodsByCategory(String category) {
        return foodRepository.findByCategory(category);
    }

    // دریافت یک غذای خاص با ID
    public Optional<Food> getFoodById(Long id) {
        return foodRepository.findById(id);
    }

    // ذخیره غذای جدید
    @Transactional
    public Food saveFood(Food food, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                String uploadDir = "src/main/resources/static/uploads/";

                // ایجاد دایرکتوری اگر وجود نداشت
                Files.createDirectories(Paths.get(uploadDir));

                // ذخیره فایل
                Path path = Paths.get(uploadDir + fileName);
                Files.copy(imageFile.getInputStream(), path);

                // ذخیره نام فایل در غذا
                food.setImage(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Could not store the image file", e);
            }
        }
        return foodRepository.save(food);
    }

    // به‌روزرسانی غذا
    @Transactional
    public Food updateFood(Food food) {
        return foodRepository.save(food);
    }

    // حذف غذا
    @Transactional
    public void deleteFood(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food id: " + id));

        // حذف تصویر قبلی اگر وجود داشت
        if (food.getImage() != null) {
            try {
                Path imagePath = Paths.get("src/main/resources/static/uploads/" + food.getImage());
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                // لاگ خطا
                e.printStackTrace();
            }
        }

        foodRepository.deleteById(id);
    }

    // جستجوی غذا بر اساس نام
//    public List<Food> searchFoods(String keyword) {
//        return foodRepository.findByNameContainingIgnoreCase(keyword);
//    }
}