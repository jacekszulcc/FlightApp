# FlightApp - Flight Search Service Backend

FlightApp is a backend application for a flight search engine, built with Java and Spring Boot. It integrates with the Amadeus API to provide flight offers and exposes the data through a REST API.

This project was developed to demonstrate key skills for a Junior Java Developer role, including API development, multi-module architecture, database management with Flyway, and containerization with Docker.

## Key Features

- REST API for searching flights and browsing search history.
- Multi-module architecture (service and database layers).
- Integration with Amadeus API (OAuth2).
- Database migrations managed by Flyway.
- Caching for search results.
- API endpoints protected by an API Key.
- Global exception handling for consistent error responses.
- Unit and integration test coverage.
- Fully containerized environment with Docker and Docker Compose.
- CI/CD pipeline using GitHub Actions.

## Technology Stack

- **Backend**: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, Maven, PostgreSQL, Flyway, Lombok, Caffeine
- **Testing**: JUnit 5, Mockito, AssertJ, Testcontainers
- **DevOps**: Git, GitHub, Docker, Docker Compose, GitHub Actions

## How to Run Locally

### Prerequisites
- Git
- Docker Desktop

### Steps

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/jacekszulcc/FlightApp.git](https://github.com/jacekszulcc/FlightApp.git)
    cd FlightApp
    ```

2.  **Create the `.env` file:**
    Create a `.env` file in the project's root directory. Copy the contents from `.env.example` and provide your credentials for the Amadeus API and a secret API key.

3.  **Run with Docker Compose:**
    Execute the following command in the root directory:
    ```bash
    docker compose up --build
    ```
    The application will be available at `http://localhost:8080`.

## API Endpoints

**Note:** All `/api/**` endpoints require an `X-API-KEY` header with the value defined in your `.env` file.

### Search for Flights

- **URL:** `/api/flights`
- **Method:** `GET`
- **Query Parameters:**
    - `originLocationCode` (String, e.g., `MAD`)
    - `destinationLocationCode` (String, e.g., `JFK`)
    - `departureDate` (String, format `YYYY-MM-DD`, e.g., `2025-11-25`)
    - `adults` (int, e.g., `1`)
- **Example (curl):**
    ```bash
    curl -X GET "http://localhost:8080/api/flights?originLocationCode=MAD&destinationLocationCode=JFK&departureDate=2025-11-25&adults=1" -H "X-API-KEY: YOUR_SECRET_API_KEY"
    ```

### Get Search History

- **URL:** `/api/history`
- **Method:** `GET`
- **Query Parameters:**
    - `page` (int, default: `0`)
    - `size` (int, default: `20`)
- **Example (curl):**
    ```bash
    curl -X GET "http://localhost:8080/api/history?page=0&size=5" -H "X-API-KEY: YOUR_SECRET_API_KEY"
    ```