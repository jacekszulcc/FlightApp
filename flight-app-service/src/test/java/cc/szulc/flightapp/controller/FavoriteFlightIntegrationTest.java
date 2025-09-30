package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.CreateFavoriteFlightRequestDto;
import cc.szulc.flightapp.dto.FavoriteFlightDto;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FavoriteFlightIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FavoriteFlightRepository favoriteFlightRepository;

    @Value("${api.security.key}")
    private String apiKey;

    @AfterEach
    void cleanup() {
        favoriteFlightRepository.deleteAll();
    }

    @Test
    void shouldAddAndRetrieveFavoriteFlight() {
        // --- Krok 1: Dodawanie ulubionego lotu (POST) ---
        String url = "http://localhost:" + port + "/api/favorites";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        CreateFavoriteFlightRequestDto requestDto = new CreateFavoriteFlightRequestDto();
        requestDto.setOrigin("GDN");
        requestDto.setDestination("KRK");
        requestDto.setDepartureDate("2025-10-01T08:00:00");
        requestDto.setArrivalDate("2025-10-01T09:00:00");
        requestDto.setCarrier("Ryanair");
        requestDto.setPrice(new BigDecimal("199.99"));

        HttpEntity<CreateFavoriteFlightRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<FavoriteFlightDto> postResponse = restTemplate.postForEntity(url, requestEntity, FavoriteFlightDto.class);

        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().getId()).isNotNull();
        assertThat(postResponse.getBody().getOrigin()).isEqualTo("GDN");
        Long favoriteId = postResponse.getBody().getId();

        HttpEntity<String> getEntity = new HttpEntity<>(headers);
        ResponseEntity<List<FavoriteFlightDto>> getResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                getEntity,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull().hasSize(1);
        assertThat(getResponse.getBody().get(0).getDestination()).isEqualTo("KRK");

        String deleteUrl = url + "/" + favoriteId;
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, getEntity, Void.class);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(favoriteFlightRepository.findById(favoriteId)).isEmpty();
    }
}
