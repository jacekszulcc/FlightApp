package cc.szulc.flightapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.SocketOption;
import java.sql.SQLOutput;
import java.util.Map;


@Service
public class FlightSearchService {

    private RestTemplate restTemplate;

    @Value("${amadeus.api.url}")
    private String apiUrl;
    @Value("${amadeus.api.authUrl}")
    private String authUrl;
    @Value("${amadeus.api.clientId}")
    private String apiClientId;
    @Value("${amadeus.api.clientSecret}")
    private String apiClientSecret;

    public FlightSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", apiClientId);
        body.add("client_secret", apiClientSecret);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, requestEntity, Map.class);

        String accessToken = (String) response.getBody().get("access_token");
        System.out.println("Pobrano nowy Access Token: " + accessToken);
        return accessToken;
    }

    public String searchForFlights() {
        /* String accessToken = getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlWithParams = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("originLocationCode", "MAD")
                .queryParam("destinationLocationCode", "JFK")
                .queryParam("departureDate", "2025-11-25")
                .queryParam("adults", 1)
                .encode()
                .toUriString();

        System.out.println("FINALNY URL: " + urlWithParams);

        ResponseEntity<String> response = restTemplate.exchange(
                urlWithParams,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody(); */
        System.out.println("Używam zasymulowanej odpowiedzi API, aby kontynuować pracę.");
        return getMockedFlightData();

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
