package com.foodhub.controller;

import com.foodhub.repository.CustomerOrderRepository;
import com.foodhub.repository.MenuItemRepository;
import com.foodhub.repository.RestaurantRepository;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SystemController {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final Environment environment;

    public SystemController(RestaurantRepository restaurantRepository,
                            MenuItemRepository menuItemRepository,
                            CustomerOrderRepository orderRepository,
                            Environment environment) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        this.environment = environment;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "FoodHub Backend API");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("database", environment.getProperty("spring.datasource.url", "h2-in-memory"));
        response.put("restaurantsCount", restaurantRepository.count());
        response.put("menuItemsCount", menuItemRepository.count());
        response.put("ordersCount", orderRepository.count());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalRestaurants", restaurantRepository.count());
        stats.put("totalMenuItems", menuItemRepository.count());
        stats.put("totalOrdersPlaced", orderRepository.count());
        return ResponseEntity.ok(stats);
    }
}

