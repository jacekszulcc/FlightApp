package cc.szulc.flightapp.service;

import cc.szulc.flightapp.client.AmadeusApiClient;
import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.repository.SearchHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    void searchForFlights_shouldCallApiClientAndSaveToHistory() throws JsonProcessingException {
        mockAuthenticatedUser();

        String origin = "WAW";
        String destination = "BER";
        String date = "2025-10-10";
        int adults = 1;

        FlightOfferResponseDto mockResponse = new FlightOfferResponseDto();

        when(amadeusApiClient.fetchFlightOffers(origin, destination, date, adults)).thenReturn(mockResponse);

        FlightOfferResponseDto actualResponse = flightSearchService.searchForFlights(origin, destination, date, adults);

        verify(amadeusApiClient, times(1)).fetchFlightOffers(origin, destination, date, adults);

        verify(searchHistoryRepository, times(1)).save(argThat(history ->
                history.getUser().equals(mockUser)
        ));

        assertThat(actualResponse).isSameAs(mockResponse);
    }

    @Test
    void getSearchHistory_shouldReturnPageOfHistory() {
        mockAuthenticatedUser();

        int page = 0;
        int size = 20;
        Pageable expectedPageable = PageRequest.of(page, size);

        SearchHistory historyEntry = new SearchHistory();
        historyEntry.setId(1L);
        historyEntry.setOriginLocationCode("WAW");
        historyEntry.setUser(mockUser);

        Page<SearchHistory> historyPage = new PageImpl<>(Collections.singletonList(historyEntry), expectedPageable, 1);

        when(searchHistoryRepository.findAllByUser(mockUser, expectedPageable)).thenReturn(historyPage);

        Page<SearchHistory> result = flightSearchService.getSearchHistory(page, size);

        verify(searchHistoryRepository, times(1)).findAllByUser(mockUser, expectedPageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getOriginLocationCode()).isEqualTo("WAW");
    }
}