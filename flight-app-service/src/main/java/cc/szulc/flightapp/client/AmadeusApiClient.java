package cc.szulc.flightapp.client;

import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.exception.AmadeusApiRequestException; // Importujemy nasz nowy wyjątek
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException; // Importujemy ogólny wyjątek RestTemplate
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@Slf4j
public class AmadeusApiClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
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

    public AmadeusApiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public FlightOfferResponseDto fetchFlightOffers(String origin, String destination, String date, int adults) throws JsonProcessingException {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String urlWithParams = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("originLocationCode", origin)
                    .queryParam("destinationLocationCode", destination)
                    .queryParam("departureDate", date)
                    .queryParam("adults", adults)
                    .encode()
                    .toUriString();

            ResponseEntity<String> response = restTemplate.exchange(urlWithParams, HttpMethod.GET, entity, String.class);
            return objectMapper.readValue(response.getBody(), FlightOfferResponseDto.class);
        } catch (RestClientException e) {
            log.error("Error while fetching flight offers from Amadeus API", e);
            throw new AmadeusApiRequestException("Failed to fetch flight offers from Amadeus API.", e);
        }
    }

    private String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return cachedToken;
        }

        try {
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
            this.tokenExpirationTime = System.currentTimeMillis() + (expiresIn * 1000L);
            return newAccessToken;
        } catch (RestClientException e) {
            log.error("Error while getting access token from Amadeus API", e);
            throw new AmadeusApiRequestException("Could not obtain access token from Amadeus API.", e);
        }
    }
}