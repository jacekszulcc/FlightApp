package cc.szulc.flightapp.service;

import cc.szulc.flightapp.client.AmadeusApiClient;
import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.repository.SearchHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightSearchServiceTest {

    @Mock
    private AmadeusApiClient amadeusApiClient;

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @InjectMocks
    private FlightSearchService flightSearchService;

    @Test
    void searchForFlights_shouldCallApiClientAndSaveToHistory() throws JsonProcessingException {
        String origin = "WAW";
        String destination = "BER";
        String date = "2025-10-10";
        int adults = 1;

        FlightOfferResponseDto mockResponse = new FlightOfferResponseDto();

        when(amadeusApiClient.fetchFlightOffers(origin, destination, date, adults)).thenReturn(mockResponse);

        FlightOfferResponseDto actualResponse = flightSearchService.searchForFlights(origin, destination, date, adults);

        verify(amadeusApiClient, times(1)).fetchFlightOffers(origin, destination, date, adults);

        verify(searchHistoryRepository, times(1)).save(any(SearchHistory.class));

        assertThat(actualResponse).isSameAs(mockResponse);
    }
}