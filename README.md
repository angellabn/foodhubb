# FoodHub - Full Stack Food Ordering Platform

A modern, responsive food ordering web app built with **Spring Boot 4**, **H2 (in-memory DB)**, **Spring Security**, and a **vanilla JS/CSS frontend**.

## 🌐 Live Demo
> Deploy link coming soon via Render.com

## 🛠️ Tech Stack
- **Backend:** Java 17, Spring Boot 4, Spring Data JPA, Spring Security, H2 Database
- **Frontend:** HTML5, CSS3 (custom), Vanilla JavaScript
- **Build:** Maven
- **Tunnel:** localtunnel (dev)

## 🚀 Run Locally

```bash
# Build
mvn -f foodhub/pom.xml clean package -DskipTests

# Run
java -jar foodhub/target/foodhub-0.0.1-SNAPSHOT.jar --server.port=8088
```

Open http://localhost:8088 — login with `user` / `password`

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Backend health check |
| GET | `/api/restaurants` | List all restaurants |
| GET | `/api/restaurants/{id}/menu` | Menu for a restaurant |
| POST | `/api/orders` | Place an order |
| GET | `/api/orders` | Order history |
| GET | `/api/convert/{dollars}` | USD → INR conversion |

## ✨ Features
- 🏪 Browse 6 restaurants across multiple cuisines
- 🍽️ View full menus with veg/non-veg indicators
- 🛒 Add to cart, adjust quantities
- 💳 Checkout with delivery address & payment method
- 📦 Order history with status tracking
- 💱 Prices displayed in **Indian Rupees (₹)**
- 🔐 Login authentication (Spring Security)
- 📱 Fully responsive design
