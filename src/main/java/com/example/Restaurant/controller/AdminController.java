package com.example.Restaurant.controller;


import com.example.Restaurant.model.User;
import com.example.Restaurant.model.Role;
import com.example.Restaurant.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // داشبورد اصلی مدیر
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("staffCount", userService.getAllUsers().size() - 1); // منهای admin
        model.addAttribute("activeStaff", userService.getActiveUsers().size());
        return "admin/dashboard";
    }

    // لیست کارکنان
    @GetMapping("/staff")
    public String staffList(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", Role.values());
        return "admin/staff-list";
    }

    // فرم اضافه کردن کارمند جدید
    @GetMapping("/staff/add")
    public String showAddStaffForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        return "admin/staff-form";
    }

    // ذخیره کارمند جدید
    @PostMapping("/staff/add")
    public String addStaff(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("success", "Staff member added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    // فرم ویرایش کارمند
    @GetMapping("/staff/edit/{id}")
    public String showEditStaffForm(@PathVariable Long id, Model model) {
        userService.getUserById(id).ifPresent(user -> {
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
        });
        return "admin/staff-form";
    }

    // به‌روزرسانی اطلاعات کارمند
    @PostMapping("/staff/edit")
    public String updateStaff(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "Staff member updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    // فعال/غیرفعال کردن کارمند
    @PostMapping("/staff/toggle/{id}")
    public String toggleStaffStatus(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getIsActive()) {
            userService.deactivateUser(id);
        } else {
            userService.activateUser(id);
        }

        return "redirect:/admin/staff";
    }
}