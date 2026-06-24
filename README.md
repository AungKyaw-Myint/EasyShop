# 🛍️ Easy Shop - Online Shopping (Spring Boot)

Easy Shop is a backend REST API for an online shopping platform built using **Spring Boot**.  
It supports user authentication, product browsing, search/filtering, cart management, and order checkout.

The frontend UI is pre-built and fully functional. All development work is focused on the backend API.

---

## 📌 Application Features

### 👤 User Management
- User registration and login
- Role-based users (User / Admin)

---

### 📦 Product Management
- View all products
- Browse products by category
- View product details (price, description, stock, etc.)
- Products grouped under categories

---

### 🔎 Search & Filter (Bug Fix Area)
- Search products by name or keyword
- Filter products by:
    - Category
    - Price range
    - Stock availability

---

### 🛒 Shopping Cart
- Add products to cart
- Update quantity
- Remove items from cart
- View user-specific cart items

---

### 💳 Orders & Checkout
- Place orders from cart
- Order summary creation
- Stores:
    - Order date
    - Shipping address
    - Shipping cost
    - Order items

---

## 📌 Project Highlights

- RESTful API development using Spring Boot
- Layered architecture (Controller, Service, Repository)
- Spring Data JPA for database operations
- Input validation and error handling
- Environment-based configuration support
- Unit and integration testing

---

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Maven
- MySQL / H2 Database (configurable)

---

## ⚙️ Prerequisites

Before running the application, ensure you have:

- Java JDK 17 or later
- Maven 3.8+
- MySQL 8.0+

---

## 🚀 Installation

### Clone the repository

```bash
git clone https://github.com/yourusername/project-name.git
cd project-name