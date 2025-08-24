package cc.szulc.flightapp.service;

import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Service
public class FlightSearchService {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private String cachedToken;
    private long tokenExpirationTime;

    @Value("${amadeus.api.url}")
    private String apiUrl;
    @Value("${amadeus.api.authUrl}")
    private String authUrl;
    @Value("${amadeus.api.clientId}")
    private String apiClientId;
    @Value("${amadeus.api.clientSecret}")
    private String apiClientSecret;

    public FlightSearchService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            System.out.println("Używam zapisanego tokena.");
            return cachedToken;
        }

        System.out.println("Brak ważnego tokena, pobieram nowy.");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", apiClientId);
        body.add("client_secret", apiClientSecret);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, requestEntity, Map.class);

        String newAccessToken = (String) response.getBody().get("access_token");
        Integer expiresIn = (Integer) response.getBody().get("expires_in");

        this.cachedToken = newAccessToken;
        this.tokenExpirationTime = System.currentTimeMillis() + (expiresIn * 1000);

        System.out.println("Pobrano i zapisano nowy token.");
        return newAccessToken;
    }

    @Cacheable("flightOffers")
    public FlightOfferResponseDto searchForFlights(String originLocationCode, String destinationLocationCode, String departureDate, int adults) throws JsonProcessingException {
        System.out.println("--- WYKONUJĘ PRAWDZIWE WYSZUKIWANIE ---");
        String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlWithParms = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("originLocationCode", originLocationCode)
                .queryParam("destinationLocationCode", destinationLocationCode)
                .queryParam("departureDate", departureDate)
                .queryParam("adults", adults)
                .encode()
                .toUriString();

        System.out.println("URL: " + urlWithParms);

        ResponseEntity<String> response = restTemplate.exchange(
                urlWithParms,
                HttpMethod.GET,
                entity,
                String.class
        );

        String jsonBody= response.getBody();
        return objectMapper.readValue(jsonBody, FlightOfferResponseDto.class);
    }

    private String getMockedFlightData() {
        return """
    {
      "data": [
        {
          "type": "flight-offer",
          "id": "1",
          "source": "GDS",
          "instantTicketingRequired": false,
          "nonHomogeneous": false,
          "oneWay": false,
          "lastTicketingDate": "2025-08-01",
          "numberOfBookableSeats": 9,
          "itineraries": [
            {
              "duration": "PT8H25M",
              "segments": [
                {
                  "departure": {
                    "iataCode": "CDG",
                    "terminal": "1",
                    "at": "2025-11-25T13:30:00"
                  },
                  "arrival": {
                    "iataCode": "JFK",
                    "terminal": "4",
                    "at": "2025-11-25T16:55:00"
                  },
                  "carrierCode": "AF",
                  "number": "006",
                  "aircraft": {
                    "code": "77W"
                  }
                }
              ]
            }
          ],
          "price": {
            "currency": "EUR",
            "total": "550.00",
            "base": "450.00"
          }
        }
      ]
    }
    """;
    }

}
