package com.example.Restaurant.controller;

import com.example.Restaurant.model.CartItem;
import com.example.Restaurant.model.Food;
import com.example.Restaurant.repository.CartItemRepository;
import com.example.Restaurant.repository.FoodRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodRepository foodRepository;

    @PostMapping("/add")
    @ResponseBody
    public String addToCart(@RequestParam Long foodId, @RequestParam Integer quantity) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid food Id:" + foodId));

        CartItem cartItem = new CartItem();
        cartItem.setFood(food);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return "Added to cart successfully";
    }

    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        List<CartItem> cartItems = cartItemRepository.findAll();

        if (cartItems.isEmpty()) {
            session.removeAttribute("cartItems"); // پاک کردن داده‌های کش‌شده
        }

        model.addAttribute("cartItems", cartItems);
        return "cart";
    }


    @PostMapping("/update")
    @ResponseBody
    public String updateCart(@RequestParam Long itemId, @RequestParam Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + itemId));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }


        return String.valueOf(getCartItemCount());
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("cartItems", cartItemRepository.findAll());
        return "checkout";
    }

    @GetMapping("/count")
    @ResponseBody
    public int getCartItemCount() {
        return ((List<CartItem>) cartItemRepository.findAll())
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }


    @GetMapping("/")
    public String getHome(Model model) {
        return "home";
    }





}




