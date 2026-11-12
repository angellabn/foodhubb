# 🚀 Guide: Deploying FoodHub to Render for a Permanent Resume Link

This project is completely configured and ready to be hosted on **Render** (free 24/7 cloud hosting with permanent HTTPS link).

---

## Step 1: Create a GitHub Repository

1. Open [github.com/new](https://github.com/new) in your browser.
2. Enter Repository name: `foodhub`
3. Keep it **Public** (recommended so recruiters can inspect code and Render can easily access it).
4. Do NOT initialize with README or .gitignore (we already created them). Click **Create repository**.
5. Copy the repository URL (e.g., `https://github.com/<your-username>/foodhub.git`).

---

## Step 2: Push Your Code from Terminal

Open PowerShell inside `C:\Users\john6\Downloads\foodhub` and run:

```powershell
git remote add origin https://github.com/<your-username>/foodhub.git
git push -u origin main
```

*(Enter your GitHub credentials / personal access token if prompted)*

---

## Step 3: Deploy on Render.com (Free)

1. Sign up or log into [Render.com](https://render.com) (you can click **Sign in with GitHub**).
2. On your Render Dashboard, click **New +** > **Web Service**.
3. Select **Build and deploy from a Git repository** > Click **Next**.
4. Connect and choose your `foodhub` repository.
5. Configure the settings:
   - **Name**: `foodhub` (or `foodhub-demo`)
   - **Language**: `Java`
   - **Branch**: `main`
   - **Region**: Any (e.g., Singapore / Oregon / Frankfurt)
   - **Build Command**:
     ```bash
     mvn clean package -DskipTests
     ```
   - **Start Command**:
     ```bash
     java -jar foodhub/target/foodhub-0.0.1-SNAPSHOT.jar
     ```
   - **Instance Type**: Select **Free**
6. Click **Deploy Web Service** at the bottom!

---

## Step 4: Your Permanent Resume Link

Render will build and deploy the app in ~3–5 minutes. Once finished, Render will provide a permanent link at the top of the page:

```
https://foodhub-xxxx.onrender.com
```

### Resume Section Example:
> **FoodHub — Full-Stack Food Ordering & Delivery Application**
> - **Live Demo**: `https://foodhub-xxxx.onrender.com`
> - **Source Code**: `https://github.com/<your-username>/foodhub`
> - **Tech Stack**: Java 21, Spring Boot 4, Spring Security, Spring Data JPA, H2 In-Memory DB, Vanilla JavaScript, CSS3
> - **Features**: Responsive storefront, restaurant directory, dynamic menu browsing, cart & checkout pipeline, currency converter (USD/INR), and role-based authentication.
