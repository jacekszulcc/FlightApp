# FlightApp - Flight Search Service Backend

FlightApp is a backend application for a flight search engine, built with Java 17 and Spring Boot 3. It integrates with the Amadeus API to provide flight offers, saves search history and favorite flights to a PostgreSQL database, and exposes data through a REST API secured with JWT.

The project was built as a working example of system integration and data processing: it connects to an external provider (Amadeus) over REST using OAuth2 client credentials, fetches flight and location data from that system, and persists search history and user-selected records in PostgreSQL. The database schema is managed through versioned Flyway migrations, access to the data is controlled by Spring Security with JWT, the whole runtime environment is containerized with Docker Compose, and every change is verified by an automated build and test pipeline in GitHub Actions.

## Key Features

* **REST API** for searching flights, airport locations, search history, and managing favorite flights.
* **Full Authentication & Authorization** based on roles (USER, ADMIN) using JWT.
* **Secure Token Management:** Implemented Access Token and Refresh Token mechanism for secure, long-term sessions.
* **Admin Panel:** Dedicated endpoints for administrators to view and manage registered users.
* **Maven Multi-Module Architecture** (`flight-app-db`, `flight-app-service`).
* **Amadeus API Integration** (OAuth2) to fetch real-time flight offers and locations.
* **Database Migrations** managed by Flyway.
* **Caching:** In-memory caching (Caffeine) for optimized user data retrieval.
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
    git clone https://github.com/jacekszulcc/FlightApp.git
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

**Note:** All `/api/**` endpoints require authentication. You must send a valid access token in the request header: `Authorization: Bearer <YOUR_ACCESS_TOKEN>`.

### Authorization (Public)

* **User Registration:**
    * **URL:** `/auth/register`
    * **Method:** `POST`
    * **Body (JSON):** `{"username": "user", "password": "password123"}`

* **Login:**
    * **URL:** `/auth/login`
    * **Method:** `POST`
    * **Body (JSON):** `{"username": "user", "password": "password123"}`
    * **Success Response:** `{"accessToken": "ey...", "refreshToken": "uuid..."}`

* **Refresh Token:**
    * **URL:** `/auth/refresh`
    * **Method:** `POST`
    * **Body (JSON):** `{"refreshToken": "your-refresh-token-uuid"}`
    * **Success Response:** `{"accessToken": "ey...", "refreshToken": "your-refresh-token-uuid"}`

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

* **Change User Status (Block/Unblock):**
    * **URL:** `/api/admin/users/{id}/status`
    * **Method:** `PATCH`
    * **Query Parameters:**
        * `enabled` (boolean, e.g., `true` or `false`)

## Status

This project is finished and is no longer being developed. The last change to the code was made in December 2025. It was built as a complete, runnable example of integrating an external REST API with a persistence layer and a containerized deployment, and it is kept here in that state as a reference.
