package com.foodhub;

import com.foodhub.controller.MenuItemController;
import com.foodhub.controller.OrderController;
import com.foodhub.controller.RestaurantController;
import com.foodhub.controller.SystemController;
import com.foodhub.dto.OrderRequest;
import com.foodhub.model.CustomerOrder;
import com.foodhub.model.MenuItem;
import com.foodhub.model.Restaurant;
import com.foodhub.repository.CustomerOrderRepository;
import com.foodhub.repository.MenuItemRepository;
import com.foodhub.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FoodhubApplicationTests {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	@Autowired
	private CustomerOrderRepository orderRepository;

	@Autowired
	private RestaurantController restaurantController;

	@Autowired
	private MenuItemController menuItemController;

	@Autowired
	private OrderController orderController;

	@Autowired
	private SystemController systemController;

	@Test
	void contextLoads() {
		assertNotNull(restaurantRepository);
		assertNotNull(menuItemRepository);
		assertNotNull(orderRepository);
		assertNotNull(restaurantController);
		assertNotNull(menuItemController);
		assertNotNull(orderController);
		assertNotNull(systemController);
	}

	@Test
	void testDataSeederPopulatedRestaurants() {
		List<Restaurant> restaurants = restaurantRepository.findAll();
		assertFalse(restaurants.isEmpty(), "DataSeeder should populate restaurants");
		assertTrue(restaurants.size() >= 6, "Expected at least 6 restaurants");
	}

	@Test
	void testHealthEndpoint() {
		ResponseEntity<Map<String, Object>> healthResponse = systemController.getHealth();
		assertEquals(HttpStatus.OK, healthResponse.getStatusCode());
		assertNotNull(healthResponse.getBody());
		assertEquals("UP", healthResponse.getBody().get("status"));
		assertEquals("FoodHub Backend API", healthResponse.getBody().get("service"));
		assertTrue((Long) healthResponse.getBody().get("restaurantsCount") >= 6);
	}

	@Test
	void testGetAllRestaurants() {
		List<Restaurant> restaurants = restaurantController.getAllRestaurants(null, null);
		assertNotNull(restaurants);
		assertFalse(restaurants.isEmpty());
	}

	@Test
	void testFilterRestaurantsByCuisine() {
		List<Restaurant> italianRestaurants = restaurantController.getAllRestaurants(null, "Italian");
		assertNotNull(italianRestaurants);
		assertFalse(italianRestaurants.isEmpty());
		assertEquals("Italian", italianRestaurants.get(0).getCuisine());
	}

	@Test
	void testGetRestaurantMenu() {
		List<Restaurant> restaurants = restaurantRepository.findAll();
		assertFalse(restaurants.isEmpty());
		Long restId = restaurants.get(0).getId();

		List<MenuItem> menu = restaurantController.getRestaurantMenu(restId);
		assertNotNull(menu);
		assertFalse(menu.isEmpty());
		assertEquals(restId, menu.get(0).getRestaurantId());
	}

	@Test
	void testPlaceOrderValidationAndCreation() {
		// Test invalid order (missing address)
		OrderRequest invalidRequest = new OrderRequest();
		invalidRequest.setCustomerName("John Doe");
		invalidRequest.setCustomerEmail("john@example.com");
		ResponseEntity<?> invalidResponse = orderController.placeOrder(invalidRequest);
		assertEquals(HttpStatus.BAD_REQUEST, invalidResponse.getStatusCode());

		// Test valid order
		OrderRequest validRequest = new OrderRequest();
		validRequest.setCustomerName("Jane Doe");
		validRequest.setCustomerEmail("jane@example.com");
		validRequest.setCustomerPhone("+1 555-0199");
		validRequest.setDeliveryAddress("100 Market St, Suite 400");
		validRequest.setPaymentMethod("CARD");

		OrderRequest.OrderItemDto item = new OrderRequest.OrderItemDto();
		item.setMenuItemId(1L);
		item.setItemName("Margherita D.O.P.");
		item.setPrice(14.99);
		item.setQuantity(2);
		validRequest.setItems(List.of(item));

		ResponseEntity<?> response = orderController.placeOrder(validRequest);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertTrue(response.getBody() instanceof CustomerOrder);

		CustomerOrder created = (CustomerOrder) response.getBody();
		assertNotNull(created.getId());
		assertEquals("Jane Doe", created.getCustomerName());
		assertEquals("CONFIRMED", created.getStatus());
		assertTrue(created.getTotalAmount() > 0);
	}
}
