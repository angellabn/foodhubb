package com.foodhub.config;

import com.foodhub.model.CustomerOrder;
import com.foodhub.model.MenuItem;
import com.foodhub.model.OrderItem;
import com.foodhub.model.Restaurant;
import com.foodhub.repository.CustomerOrderRepository;
import com.foodhub.repository.MenuItemRepository;
import com.foodhub.repository.RestaurantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerOrderRepository orderRepository;

    public DataSeeder(RestaurantRepository restaurantRepository,
                      MenuItemRepository menuItemRepository,
                      CustomerOrderRepository orderRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (restaurantRepository.count() > 0) {
            return;
        }

        // 1. Italian Trattoria
        Restaurant r1 = new Restaurant(
                "Bella Napoli Pizzeria",
                "Authentic Neapolitan wood-fired pizzas, handmade pastas, and rustic Italian specialties crafted with imported Italian flour and San Marzano tomatoes.",
                "Italian",
                4.9,
                "25-35 min",
                2.49,
                "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80",
                "142 Little Italy Way, Downtown",
                true
        );

        // 2. Gourmet Burger Joint
        Restaurant r2 = new Restaurant(
                "The Burger Foundry",
                "Artisanal smashed smashburgers, dry-aged beef patties, crispy brioche buns, and gourmet loaded truffle fries.",
                "Burgers",
                4.8,
                "20-30 min",
                1.99,
                "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=800&q=80",
                "88 Oak Avenue, Midtown",
                true
        );

        // 3. Japanese Sushi & Ramen
        Restaurant r3 = new Restaurant(
                "Sakura Tokyo Lounge",
                "Fresh premium sashimi, specialty sushi rolls, authentic slow-simmered tonkotsu ramen, and crispy gyoza.",
                "Japanese",
                4.9,
                "30-40 min",
                3.49,
                "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?auto=format&fit=crop&w=800&q=80",
                "51 Cherry Blossom Lane, Uptown",
                true
        );

        // 4. Mexican Taqueria
        Restaurant r4 = new Restaurant(
                "El Fuego Cantina",
                "Authentic Mexican street tacos, cheesy birria quesatacos, fresh guacamole, and sizzling fajitas with homemade salsas.",
                "Mexican",
                4.7,
                "15-25 min",
                1.99,
                "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?auto=format&fit=crop&w=800&q=80",
                "204 Fiesta Blvd, Westside",
                true
        );

        // 5. Healthy Green Bistro
        Restaurant r5 = new Restaurant(
                "Green Vitality Bowls",
                "Organic superfood grain bowls, vibrant seasonal salads, fresh cold-pressed wellness elixirs, and dairy-free smoothies.",
                "Healthy",
                4.8,
                "15-25 min",
                2.99,
                "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&w=800&q=80",
                "12 Eco Parkway, Green District",
                true
        );

        // 6. Artisan Bakery & Desserts
        Restaurant r6 = new Restaurant(
                "Sweet Craving Patisserie",
                "French artisanal pastries, decadent molten chocolate lava cakes, handcrafted macarons, and specialty coffee brews.",
                "Desserts",
                4.9,
                "20-30 min",
                1.49,
                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=800&q=80",
                "77 Sugar Street, Old Town",
                true
        );

        restaurantRepository.saveAll(List.of(r1, r2, r3, r4, r5, r6));

        // Seed Menu Items
        List<MenuItem> items = List.of(
                // Restaurant 1: Bella Napoli
                new MenuItem("Margherita D.O.P.", "San Marzano tomato sauce, fresh buffalo mozzarella, fresh basil, extra virgin olive oil.", 14.99, "Pizza", "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?auto=format&fit=crop&w=800&q=80", true, r1.getId()),
                new MenuItem("Diavola Spicy Pepperoni", "Spicy calabrian salami, crushed red pepper flakes, fresh mozzarella, tomato passata.", 16.99, "Pizza", "https://images.unsplash.com/photo-1628840042765-356cda07504e?auto=format&fit=crop&w=800&q=80", false, r1.getId()),
                new MenuItem("Truffle Wild Mushroom", "Creamy fontina cheese, wild portobello and cremini mushrooms, white truffle oil, thyme.", 18.50, "Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=800&q=80", true, r1.getId()),
                new MenuItem("Classic Tiramisu", "Espresso-soaked ladyfingers, velvety mascarpone cream, Dutch cocoa powder.", 7.99, "Desserts", "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&w=800&q=80", true, r1.getId()),

                // Restaurant 2: The Burger Foundry
                new MenuItem("The Foundry Double Smash", "Double smashed Angus beef patties, aged cheddar, caramelized onions, secret sauce on toasted brioche.", 13.99, "Burgers", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=800&q=80", false, r2.getId()),
                new MenuItem("Smoked Bacon & BBQ Burger", "Applewood smoked bacon, crispy onion rings, sharp cheddar, smoked bourbon BBQ sauce.", 15.49, "Burgers", "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?auto=format&fit=crop&w=800&q=80", false, r2.getId()),
                new MenuItem("Truffle Parmesan Herb Fries", "Golden shoestring fries tossed in white truffle oil, grated parmesan, and rosemary sea salt.", 6.99, "Sides", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=800&q=80", true, r2.getId()),
                new MenuItem("Craft Vanilla Bean Milkshake", "Rich whole milk ice cream spun with Madagascar vanilla beans and whipped cream.", 5.99, "Beverages", "https://images.unsplash.com/photo-1572490122747-3968b75cc699?auto=format&fit=crop&w=800&q=80", true, r2.getId()),

                // Restaurant 3: Sakura Tokyo Lounge
                new MenuItem("Dragon Specialty Roll (8 pcs)", "Tempura shrimp, cucumber, topped with ripe avocado, barbecue eel sauce, spicy mayo.", 16.50, "Sushi", "https://images.unsplash.com/photo-1617196034796-73dfa7b1fd56?auto=format&fit=crop&w=800&q=80", false, r3.getId()),
                new MenuItem("Salmon Nigiri Flight (6 pcs)", "Hand-selected fresh Atlantic salmon over seasoned sushi rice with wasabi.", 14.00, "Sushi", "https://images.unsplash.com/photo-1611143669185-af224c5e3252?auto=format&fit=crop&w=800&q=80", false, r3.getId()),
                new MenuItem("Tonkotsu Chashu Ramen", "24-hour pork bone broth, springy ramen noodles, braised pork belly, marinated egg, nori.", 15.99, "Asian", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&w=800&q=80", false, r3.getId()),
                new MenuItem("Crispy Pork Gyoza (6 pcs)", "Pan-seared Japanese dumplings served with scallion soy dipping sauce.", 7.50, "Sides", "https://images.unsplash.com/photo-1498654896293-37aacf113fd9?auto=format&fit=crop&w=800&q=80", false, r3.getId()),

                // Restaurant 4: El Fuego Cantina
                new MenuItem("Birria Quesatacos (3 pcs)", "Slow-braised beef birria, melted Oaxaca cheese in crispy corn tortillas, rich dipping consommé.", 14.50, "Mexican", "https://images.unsplash.com/photo-1551504734-5ee1c4a1479b?auto=format&fit=crop&w=800&q=80", false, r4.getId()),
                new MenuItem("Carne Asada Street Tacos (3 pcs)", "Citrus marinated flank steak, diced white onions, fresh cilantro, roasted salsa verde.", 12.99, "Mexican", "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?auto=format&fit=crop&w=800&q=80", false, r4.getId()),
                new MenuItem("Churro Bites & Dulce de Leche", "Crispy fried cinnamon sugar pastry bites served with warm Mexican caramel sauce.", 6.50, "Desserts", "https://images.unsplash.com/photo-1624300629298-e9de39c13be5?auto=format&fit=crop&w=800&q=80", true, r4.getId()),

                // Restaurant 5: Green Vitality Bowls
                new MenuItem("Harvest Roasted Buddha Bowl", "Warm quinoa, roasted sweet potatoes, spiced chickpeas, massaged kale, creamy lemon tahini.", 13.50, "Healthy", "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&w=800&q=80", true, r5.getId()),
                new MenuItem("Super Green Detox Smoothie", "Spinach, organic kale, green apple, ginger, cucumber, fresh lime, coconut water.", 7.00, "Beverages", "https://images.unsplash.com/photo-1610970881699-44a5587cabec?auto=format&fit=crop&w=800&q=80", true, r5.getId()),

                // Restaurant 6: Sweet Craving Patisserie
                new MenuItem("Belgian Molten Lava Cake", "Warm chocolate fondant cake with a decadent oozing center, served with vanilla cream.", 8.99, "Desserts", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=800&q=80", true, r6.getId()),
                new MenuItem("Assorted French Macarons (6 pcs)", "Pistachio, Raspberry, Salted Butter Caramel, Dark Chocolate, Lavender, Lemon.", 12.00, "Desserts", "https://images.unsplash.com/photo-1569864358642-9d1684040f43?auto=format&fit=crop&w=800&q=80", true, r6.getId())
        );

        menuItemRepository.saveAll(items);

        // Seed a sample order so Order History is never empty!
        CustomerOrder initialOrder = new CustomerOrder(
                "Alex Morgan",
                "alex.morgan@example.com",
                "+1 (555) 349-8812",
                "742 Evergreen Terrace, Springfield",
                25.96,
                "DELIVERED",
                "CARD"
        );
        initialOrder.addItem(new OrderItem(items.get(0).getId(), items.get(0).getName(), items.get(0).getPrice(), 1));
        initialOrder.addItem(new OrderItem(items.get(3).getId(), items.get(3).getName(), items.get(3).getPrice(), 1));
        orderRepository.save(initialOrder);
    }
}

