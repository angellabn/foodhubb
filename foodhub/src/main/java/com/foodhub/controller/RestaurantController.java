package com.foodhub.controller;

import com.foodhub.model.MenuItem;
import com.foodhub.model.Restaurant;
import com.foodhub.repository.MenuItemRepository;
import com.foodhub.repository.RestaurantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantController(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants(@RequestParam(required = false) String search,
                                              @RequestParam(required = false) String cuisine) {
        if (search != null && !search.trim().isEmpty()) {
            return restaurantRepository.findByNameContainingIgnoreCase(search.trim());
        }
        if (cuisine != null && !cuisine.trim().isEmpty()) {
            return restaurantRepository.findByCuisineContainingIgnoreCase(cuisine.trim());
        }
        return restaurantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        return restaurantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/menu")
    public List<MenuItem> getRestaurantMenu(@PathVariable Long id) {
        return menuItemRepository.findByRestaurantId(id);
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant saved = restaurantRepository.save(restaurant);
        return ResponseEntity.ok(saved);
    }
}

