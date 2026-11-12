# 🍔 FoodHub — Full-Stack Food Ordering & Delivery Platform

FoodHub is a modern, responsive web application and Spring Boot backend service for online food ordering and delivery.

---

## 🌐 Public Live Link

- **Public URL**: [https://warm-snakes-attack.loca.lt](https://warm-snakes-attack.loca.lt)
- **Tunnel Password / IP**: `45.119.30.72` *(If Localtunnel displays an initial verification prompt, paste this password and click submit)*
- **Local URL**: [http://localhost:8088](http://localhost:8088)
- **H2 Database Console**: [http://localhost:8088/h2-console](http://localhost:8088/h2-console) *(JDBC URL: `jdbc:h2:mem:foodhubdb`, User: `sa`, Password: empty)*

---

## 🛠️ Inconsistencies Found & How They Were Fixed

During code analysis and testing, several critical inconsistencies were identified and resolved:

### 1. Database Configuration & Immediate Application Crash
- **Problem**: `pom.xml` included `spring-boot-starter-data-jpa` and `mysql-connector-j`, but `application.properties` had zero database properties configured, and no MySQL database was running locally. On startup and during tests, Spring Boot crashed with `DataSourceBeanCreationException: Failed to determine a suitable driver class`.
- **Fix**: Added `com.h2database:h2` embedded database dependency and configured an in-memory database (`jdbc:h2:mem:foodhubdb`) as the default runtime data store. This allows the application to run out-of-the-box with zero setup, while keeping optional MySQL properties clearly documented for production.

### 2. Port Collision with Background Jenkins Service
- **Problem**: The default Spring Boot port `8080` was already occupied by a background Jenkins CI service (`Jetty/12.1.8`), causing HTTP requests to fail with `403 Forbidden` authentication errors.
- **Fix**: Reconfigured FoodHub to run on port `8088` in `application.properties`, ensuring seamless execution without conflicts.

### 3. Redundant Nested Directory Structure
- **Problem**: The project was nested inside `foodhub/foodhub/`, meaning Maven commands and IDE project loaders looking at the workspace root could not find `pom.xml`.
- **Fix**: Created a root Maven project aggregator (`pom.xml`) at the workspace root and linked `mvnw` wrapper scripts so building or testing from either the root folder or the subfolder works seamlessly.

### 4. Missing Implementation & 404 Whitelabel Error
- **Problem**: The initial project was an empty starter skeleton with zero entities, zero controllers, zero repositories, and empty `static` resources. Visiting the app resulted in a 404 Whitelabel Error page.
- **Fix**: 
  - Created JPA entities (`Restaurant`, `MenuItem`, `CustomerOrder`, `OrderItem`).
  - Created Spring Data JPA repositories with custom query methods.
  - Created REST controllers for restaurants, menus, orders, system health, and statistics.
  - Implemented `DataSeeder` (`CommandLineRunner`) to automatically populate 6 popular restaurants and 19+ menu items with high-resolution imagery and realistic pricing.
  - Built a modern, responsive single-page frontend (`index.html`, `style.css`, `app.js`) with restaurant filtering, menu modals, interactive cart drawer, and order checkout with live status tracking.

### 5. Broken Maven Wrapper & Test Execution
- **Problem**: `mvnw.cmd` failed offline due to missing wrapper JAR download mechanisms, and JUnit Platform Launcher clashed with ByteBuddy dynamic agent loading under Windows sandbox constraints.
- **Fix**: Specified exact surefire plugin and dependency management configurations in `pom.xml`, allowing all 7 unit and integration tests to pass 100% cleanly (`mvn test`).

---

## 🚀 Running the Project Locally

### Prerequisites
- Java 17 or Java 21 JDK
- Maven 3.x (or use the provided wrapper)

### Commands
```powershell
# 1. Run all unit and integration tests
mvn test

# 2. Package the executable JAR
mvn package -DskipTests

# 3. Start the application
java -jar foodhub\target\foodhub-0.0.1-SNAPSHOT.jar --server.port=8088

# 4. Or use the automated startup & tunnel script
powershell -ExecutionPolicy Bypass -File .\start-foodhub.ps1
```

---

## 📡 REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Backend status, database type, and entity counts |
| `GET` | `/api/stats` | System statistics (restaurants, menu items, orders) |
| `GET` | `/api/restaurants` | List all restaurants (supports `?cuisine=` and `?search=`) |
| `GET` | `/api/restaurants/{id}` | Get specific restaurant details |
| `GET` | `/api/restaurants/{id}/menu` | Get all menu items for a restaurant |
| `GET` | `/api/menu-items` | List menu items (supports `?category=`) |
| `GET` | `/api/orders` | Get recent customer orders |
| `POST` | `/api/orders` | Place a new customer order |
| `PATCH` | `/api/orders/{id}/status` | Update order delivery status |

