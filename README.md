# FlightApp - Flight Search Service Backend

FlightApp is a backend application for a flight search engine, built with Java 17 and Spring Boot 3. It integrates with the Amadeus API to provide flight offers, saves search history and favorite flights to a PostgreSQL database, and exposes data through a REST API secured with JWT.

This project was developed to demonstrate key skills for a Junior Java Developer role, including API development, multi-module architecture, database management (Flyway), security implementation (Spring Security, JWT), and containerization (Docker).

## Key Features

* **REST API** for searching flights, airport locations, search history, and managing favorite flights.
* **Full Authentication & Authorization** based on roles (USER, ADMIN) using JWT.
* **Admin Panel:** Dedicated endpoints for administrators to view registered users.
* **Maven Multi-Module Architecture** (`flight-app-db`, `flight-app-service`).
* **Amadeus API Integration** (OAuth2) to fetch real-time flight offers and locations.
* **Database Migrations** managed by Flyway.
* **Soft Delete:** Favorite flights are not physically removed from the database but marked as deleted to preserve data integrity.
* **Global Exception Handling** for consistent error responses.
* **Automatic Admin Account** creation on application startup.
* **Unit & Integration Testing** coverage (JUnit 5, Mockito, Spring Boot Test).
* **Containerization:** Fully configured runtime environment using Docker and Docker Compose.
* **CI/CD:** Automated build and test pipeline using GitHub Actions.
* **API Documentation:** Automatically generated via Swagger/OpenAPI.

## Technology Stack

* **Backend:** Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, Maven
* **Database:** PostgreSQL, Flyway
* **Testing:** JUnit 5, Mockito, AssertJ
* **Security:** JWT (jjwt)
* **DevOps:** Git, GitHub, Docker, Docker Compose, GitHub Actions
* **Other:** Lombok, Caffeine (Cache), Springdoc-OpenAPI

## How to Run Locally

### Prerequisites
* Git
* Docker Desktop

### Steps

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/jacekszulcc/FlightApp.git](https://github.com/jacekszulcc/FlightApp.git)
    cd FlightApp
    ```

2.  **Create the `.env` file:**
    In the project's root directory, create a `.env` file. Copy the contents from `.env.example` and fill in your Amadeus API keys, JWT secret key, and admin account details.

3.  **Run with Docker Compose:**
    Execute the following command in the root directory:
    ```bash
    docker compose up --build
    ```
    The application will be available at `http://localhost:8080`.
    Swagger API documentation will be available at `http://localhost:8080/swagger-ui.html`.

## API Endpoints

**Note:** All `/api/**` endpoints require authentication. You must send a valid JWT in the request header: `Authorization: Bearer <YOUR_TOKEN>`.

### Authorization (Public)

* **User Registration:**
    * **URL:** `/auth/register`
    * **Method:** `POST`
    * **Body (JSON):** `{"username": "user", "password": "password123"}`

* **Login:**
    * **URL:** `/auth/login`
    * **Method:** `POST`
    * **Body (JSON):** `{"username": "user", "password": "password123"}`
    * **Success Response:** `{"token": "eyJhbGciOi..."}`

### Search (Requires USER or ADMIN Role)

* **Search Flights:**
    * **URL:** `/api/flights`
    * **Method:** `GET`
    * **Query Parameters:**
        * `originLocationCode` (String, e.g., `WAW`)
        * `destinationLocationCode` (String, e.g., `JFK`)
        * `departureDate` (String, `YYYY-MM-DD` format, e.g., `2025-11-25`)
        * `adults` (int, e.g., `1`)

* **Search Locations (Airports/Cities):**
    * **URL:** `/api/locations`
    * **Method:** `GET`
    * **Query Parameters:**
        * `keyword` (String, e.g., `Warsaw` or `JFK`)

### History and Favorites (User Context)
*These endpoints return data specific to the currently authenticated user.*

* **Get Search History (paginated):**
    * **URL:** `/api/history`
    * **Method:** `GET`
    * **Query Parameters:**
        * `page` (int, default: `0`)
        * `size` (int, default: `20`)

* **Get Favorite Flights (paginated):**
    * **URL:** `/api/favorites`
    * **Method:** `GET`
    * **Query Parameters:**
        * `page` (int, default: `0`)
        * `size` (int, default: `20`)

* **Add to Favorites:**
    * **URL:** `/api/favorites`
    * **Method:** `POST`
    * **Body (JSON):** `CreateFavoriteFlightRequestDto`

* **Delete from Favorites (Soft Delete):**
    * **URL:** `/api/favorites/{id}`
    * **Method:** `DELETE`

### Admin Management (Requires ADMIN Role)

* **Get All Users (paginated):**
    * **URL:** `/api/admin/users`
    * **Method:** `GET`
    * **Query Parameters:**
        * `page` (int, default: `0`)
        * `size` (int, default: `20`)
    * **Response:** Returns a paginated list of users (safe DTOs without passwords).
