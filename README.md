# FlightApp - Backend serwisu wyszukiwania lotów

FlightApp to aplikacja backendowa napisana w Javie i Spring Boot, stworzona jako projekt portfolio. Aplikacja symuluje działanie serwisu do wyszukiwania ofert lotów, integrując się z zewnętrznym dostawcą danych (Amadeus) i udostępniając wyniki poprzez REST API.

Głównym celem projektu było zademonstrowanie kluczowych umiejętności wymaganych na stanowisku Junior Java Developera, w tym m.in. budowanie REST API, obsługa zależności, konteneryzacja oraz pisanie testów.

## ✨ Kluczowe funkcje

* **REST API** do wyszukiwania ofert lotów.
* **Dynamiczne parametry wyszukiwania** (miasto wylotu/przylotu, data, liczba pasażerów).
* **Integracja z zewnętrznym API** z obsługą autoryzacji **OAuth2**.
* **Inteligentne cachowanie tokena** dostępowego w celu optymalizacji zapytań.
* **Cachowanie wyników wyszukiwania** w celu zwiększenia wydajności.
* **Globalna obsługa wyjątków** zapewniająca spójne i czytelne odpowiedzi o błędach.
* **Pełne pokrycie testami** (jednostkowymi i integracyjnymi).
* Aplikacja w pełni **skonteneryzowana** za pomocą Dockera.

## 🛠️ Użyte technologie

* **Backend:**
    * Java 17
    * Spring Boot 3
    * Spring Web
    * Spring Cache
    * Maven
    * Lombok
* **Testowanie:**
    * JUnit 5
    * Mockito
    * AssertJ
* **DevOps:**
    * Git & GitHub
    * Docker

## 🚀 Jak uruchomić projekt lokalnie (zalecane)

Najprostszym sposobem na uruchomienie aplikacji jest użycie Dockera.

### Wymagania
* Git
* Docker Desktop

### Kroki

1.  **Sklonuj repozytorium:**
    ```bash
    git clone [https://github.com/jacekszulcc/FlightApp.git](https://github.com/jacekszulcc/FlightApp.git)
    cd FlightApp
    ```

2.  **Stwórz plik `application.properties`:**
    W katalogu `src/main/resources/` stwórz plik `application.properties` i uzupełnij go swoimi kluczami z [Amadeus for Developers](https://developers.amadeus.com/).
    ```properties
    amadeus.api.url=[https://test.api.amadeus.com/v2/shopping/flight-offers](https://test.api.amadeus.com/v2/shopping/flight-offers)
    amadeus.api.authUrl=[https://test.api.amadeus.com/v1/security/oauth2/token](https://test.api.amadeus.com/v1/security/oauth2/token)
    amadeus.api.clientId=TWOJ_CLIENT_ID
    amadeus.api.clientSecret=TWOJ_CLIENT_SECRET
    ```

3.  **Zbuduj obraz Dockera:**
    W głównym katalogu projektu uruchom komendę:
    ```bash
    docker build -t flightapp .
    ```

4.  **Uruchom kontener:**
    ```bash
    docker run -p 8080:8080 flightapp
    ```
    Aplikacja będzie dostępna pod adresem `http://localhost:8080`.

## 📖 API Endpoints

### Wyszukiwanie lotów

* **URL:** `/api/flights`
* **Metoda:** `GET`
* **Parametry zapytania:**
    * `originLocationCode` (String, np. `MAD`)
    * `destinationLocationCode` (String, np. `JFK`)
    * `departureDate` (String, format `YYYY-MM-DD`, np. `2025-11-25`)
    * `adults` (int, np. `1`)
* **Przykład użycia (curl):**
    ```bash
    curl -X GET "http://localhost:8080/api/flights?originLocationCode=MAD&destinationLocationCode=JFK&departureDate=2025-11-25&adults=1"
    ```