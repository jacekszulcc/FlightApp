package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.RestPage;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.repository.SearchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchHistoryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @BeforeEach
    void cleanup() {
        searchHistoryRepository.deleteAll();
    }

    @Test
    void shouldReturnSavedSearchHistory() {
        SearchHistory testEntry = new SearchHistory();
        testEntry.setOriginLocationCode("WAW");
        testEntry.setDestinationLocationCode("JFK");
        testEntry.setDepartureDate("2025-12-01");
        testEntry.setAdults(2);
        testEntry.setSearchTimestamp(LocalDateTime.now());
        searchHistoryRepository.save(testEntry);

        String url = "http://localhost:" + port + "/api/history";

        ParameterizedTypeReference<RestPage<SearchHistory>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<RestPage<SearchHistory>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<SearchHistory> historyList = response.getBody().getContent();

        assertThat(historyList).hasSize(1);
        assertThat(historyList.get(0).getOriginLocationCode()).isEqualTo("WAW");
        assertThat(historyList.get(0).getDestinationLocationCode()).isEqualTo("JFK");
    }
}
