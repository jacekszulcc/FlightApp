# FlightApp - Flight Search Service Backend

FlightApp is a backend application for a flight search engine, built with Java 17 and Spring Boot 3. It integrates with the Amadeus API to provide flight offers, saves search history and favorite flights to a PostgreSQL database, and exposes data through a REST API secured with JWT.

The project was built as a working example of system integration and data processing: it connects to an external provider (Amadeus) over REST using OAuth2 client credentials, fetches flight and location data from that system, and persists search history and user-selected records in PostgreSQL. The database schema is managed through versioned Flyway migrations, access to the data is controlled by Spring Security with JWT, the whole runtime environment is containerized with Docker Compose, and every change is verified by an automated build and test pipeline in GitHub Actions.

## Key Features

* **REST API** for searching flights, airport locations, search history, and managing favorite flights.
* **Authentication & Authorization** based on roles (USER, ADMIN) using JWT. Every request is authorized from the token alone — its subject, signature and expiry — so an access token stays valid for its full 24-hour lifetime and is not re-checked against the account's current state.
* **Token Management:** Access Token and Refresh Token mechanism, letting a client stay signed in for seven days without resending credentials. Issued tokens cannot be revoked: there is no logout endpoint, and `/auth/refresh` returns the same refresh token rather than rotating it.
* **Admin Panel:** Dedicated endpoints for administrators to list registered users and to enable or disable an account. Disabling an account blocks the password login path; it does not invalidate access or refresh tokens already issued to that user.
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

* **Change Password:**
    * **URL:** `/api/users/change-password`
    * **Method:** `POST`
    * **Body (JSON):** `{"oldPassword": "OldPass123", "newPassword": "NewPass123"}`
    * **Note:** The new password must be 8-100 characters and contain at least one digit, one lowercase and one uppercase letter.

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

Feature development closed on 6 December 2025 and no further features are planned. The project is still maintained: the last change to the code, on 6 August 2026, stopped `findByUsername` caching empty results, which had been hiding the admin account created at startup until the cache entry expired. It was built as a complete, runnable example of integrating an external REST API with a persistence layer and a containerized deployment. What follows is where it stops.

## Scope and limitations

This was built as a portfolio project and the list below says plainly where it stops. Stating that is more useful than leaving it to be discovered.

**Not present at all**

- **No rate limiting and no resilience layer.** Nothing tracks how many requests go to Amadeus or reacts to a 429, and there is no retry, circuit breaker or fallback — neither `bucket4j` nor `resilience4j` nor `spring-retry` is on the classpath.
- **No HTTP timeouts.** `AppConfig` returns a plain `new RestTemplate()`, so connect and read timeouts are left unconfigured. If Amadeus accepts a connection and then stalls, the request thread stays blocked.
- **No tests for several components.** `AmadeusApiClient`, `JwtService`, `JwtAuthFilter`, `GlobalExceptionHandler` and `UserController` have none, and neither do `refreshToken`, `createRefreshToken`, `changePassword`, `getAllUsers` and `changeUserStatus` in `AuthService`. `AuthServiceTest` covers `register` and `login` only.
- **No HTTP-level stubbing.** There is no WireMock, `MockRestServiceServer` or Testcontainers, so the Amadeus HTTP layer is never executed by a test — the client is mocked at the Java boundary, leaving request building, token caching and the error branches uncovered.
- **No coverage report.** The JaCoCo plugin is declared in both POMs without `<executions>`, so `prepare-agent` never binds and no report is produced.
- **No catch-all in `GlobalExceptionHandler`.** Four exception types that the code actually throws have no handler and reach the client as a bare 500: `EntityNotFoundException`, `IllegalArgumentException`, `IllegalStateException` and `JsonProcessingException`.

**Known weaknesses in what is there**

- A blocked user keeps their access. `JwtService.isTokenValid` checks the subject and expiry but not `isEnabled`, and `refreshToken` does not check it either. An access token lives 24 hours and a refresh token seven days, so a disabled account keeps working and keeps renewing itself for a week. Blocking takes effect on the password login path and nowhere else.
- `@Cacheable` on `searchForFlights` covers the method that writes search history, so a cache hit skips the write. The key does not include the user, so an identical query from another user within the ten-minute window returns that other user's result.
- No logout endpoint. The only way to drop a refresh token is to log in again, because `createRefreshToken` deletes the previous one first; a user cannot end a session deliberately, and an administrator cannot end one at all without deleting the row by hand. An access token cannot be withdrawn even then — it is stateless and stays valid until it expires.
- `/auth/refresh` returns the same refresh token instead of rotating it, so one long-lived value is replayed on every renewal for the whole seven-day window.
- `cachedToken` and `tokenExpirationTime` in `AmadeusApiClient` are plain fields written from request-handling threads without synchronisation, and the `access_token` is read from the response without a null check.
- The `page` and `size` parameters are not validated. `page=-1` ends in an uncaught `IllegalArgumentException` and a 500 response.
- The database password is written directly into `application.properties` and `docker-compose.yml`, although the remaining secrets are supplied through `.env`.
- No indexes on the foreign key columns `favorite_flight.user_id`, `search_history.user_id` and `refresh_token.user_id`, all of which are filtered on in every per-user query. PostgreSQL does not index foreign keys automatically.
- `ddl-auto=update` is enabled alongside the Flyway migrations.
- The Amadeus URLs are hardcoded to the test environment.
- Departure and arrival dates are stored as `VARCHAR(255)` by migrations V1 and V2.
- The `Dockerfile` has no `USER` instruction and the image is based on a JDK rather than a JRE.
- Polish messages in `FavoriteFlightService` and `FlightSearchService`.
- Public test endpoints `/auth/test` and `/api/admin/test`.
