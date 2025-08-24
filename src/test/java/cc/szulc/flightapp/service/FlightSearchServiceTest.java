package cc.szulc.flightapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightSearchServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FlightSearchService flightSearchService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(flightSearchService, "authUrl", "http://test-url.com");
        ReflectionTestUtils.setField(flightSearchService, "apiClientId", "test-id");
        ReflectionTestUtils.setField(flightSearchService, "apiClientSecret", "test-secret");
    }

    @Test
    void shouldReturnAccessTokenWhenApiCallIsSuccessful() {
        Map<String, Object> testResponse = Map.of(
                "access_token", "test-token-123",
                "expires_in", 1799
        );
        ResponseEntity<Map> testResponseEntity = ResponseEntity.ok(testResponse);

        when(restTemplate.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(testResponseEntity);

        String accessToken = flightSearchService.getAccessToken();

        assertThat(accessToken).isEqualTo("test-token-123");
    }
}