package com.foodhub.controller;

import com.foodhub.dto.OrderRequest;
import com.foodhub.model.CustomerOrder;
import com.foodhub.model.OrderItem;
import com.foodhub.repository.CustomerOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final CustomerOrderRepository orderRepository;

    public OrderController(CustomerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<CustomerOrder> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrder> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty() ||
            request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty() ||
            request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email, and delivery address are required."));
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order must contain at least one item."));
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(request.getCustomerName().trim());
        order.setCustomerEmail(request.getCustomerEmail().trim());
        order.setCustomerPhone(request.getCustomerPhone() != null ? request.getCustomerPhone().trim() : "");
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CARD");
        order.setStatus("CONFIRMED");

        double subtotal = 0.0;
        for (OrderRequest.OrderItemDto itemDto : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(itemDto.getMenuItemId());
            orderItem.setItemName(itemDto.getItemName());
            orderItem.setPrice(itemDto.getPrice());
            orderItem.setQuantity(itemDto.getQuantity() != null && itemDto.getQuantity() > 0 ? itemDto.getQuantity() : 1);
            order.addItem(orderItem);

            subtotal += (orderItem.getPrice() * orderItem.getQuantity());
        }

        double deliveryFee = 2.99;
        double tax = subtotal * 0.05;
        double total = subtotal + deliveryFee + tax;
        order.setTotalAmount(Math.round(total * 100.0) / 100.0);

        CustomerOrder savedOrder = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status field is required."));
        }

        return orderRepository.findById(id).map(order -> {
            order.setStatus(newStatus.toUpperCase().trim());
            CustomerOrder updated = orderRepository.save(order);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}

