StockWise Inventory Management API

A RESTful inventory management system built with Spring Boot that allows users to create, retrieve, update, and delete inventory items while enforcing validation rules and unique SKU constraints.

This project was developed as part of the CST-452 Capstone Project.

Overview

StockWise is designed to demonstrate modern backend development practices using a layered architecture. The application exposes a REST API that allows clients to manage inventory items, including tracking quantities, costs, and SKU identifiers.

The system follows a standard enterprise architecture:

Client
  ↓
REST Controller
  ↓
Service Layer
  ↓
Repository Layer
  ↓
H2 Database
Technologies Used

Java 21

Spring Boot

Spring Web

Spring Data JPA

Hibernate

H2 In-Memory Database

JUnit 5

MockMvc

Maven

Swagger / OpenAPI

Project Structure
stockwise
│
├── src/main/java/com/stockwise
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   └── exception
│
├── src/test/java/com/stockwise
│   ├── controller tests
│   └── service tests
│
└── pom.xml
Features

The API supports full CRUD functionality for inventory items.

Create Item
POST /api/items
Retrieve All Items
GET /api/items
Retrieve Item by ID
GET /api/items/{id}
Retrieve Item by SKU
GET /api/items/sku/{sku}
Update Item
PUT /api/items/{id}
Delete Item
DELETE /api/items/{id}
Example API Response
GET /api/items

Example response:

[
  {
    "id": 1,
    "name": "Hammer",
    "sku": "SKU-001",
    "quantityOnHand": 25,
    "unitCost": 9.99,
    "active": true
  }
]
Validation Rules

The system enforces the following business rules:

Item name must not be empty

SKU must be unique

Quantity must be greater than or equal to zero

Unit cost must be a positive value

Invalid input returns HTTP 400

Missing records return HTTP 404

Duplicate SKU returns HTTP 409

Running the Application

Clone the repository:

git clone https://github.com/YOUR_USERNAME/stockwise.git

Navigate to the project directory:

cd stockwise

Run the application:

mvn spring-boot:run

The application will start on:

http://localhost:8080
API Documentation

Swagger/OpenAPI documentation is available at:

http://localhost:8080/swagger-ui/index.html

This interface allows you to explore and test the API endpoints interactively.

Database

The application uses an H2 in-memory database.

H2 Console:

http://localhost:8080/h2-console

JDBC URL:

jdbc:h2:mem:stockwise
Testing

The project includes both unit tests and integration tests.

Testing tools used:

JUnit 5

MockMvc

Tests verify:

CRUD functionality

validation rules

error handling

API responses

Example test results:

11 Integration Tests Passed
10 Service Layer Tests Passed
Future Improvements

Potential enhancements include:

User authentication and authorization

Persistent database (PostgreSQL or MySQL)

Inventory reporting features

Frontend dashboard

Pagination and filtering improvements

Author

Austin Beck
CST-452 Capstone Project
Grand Canyon University
