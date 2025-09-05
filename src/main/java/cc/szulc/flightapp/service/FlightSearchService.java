package cc.szulc.flightapp.service;

import cc.szulc.flightapp.client.AmadeusApiClient;
import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.repository.SearchHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FlightSearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final AmadeusApiClient amadeusApiClient;

    public FlightSearchService(SearchHistoryRepository searchHistoryRepository, AmadeusApiClient amadeusApiClient) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.amadeusApiClient = amadeusApiClient;
    }

    @Cacheable("flightOffers")
    public FlightOfferResponseDto searchForFlights(String originLocationCode, String destinationLocationCode, String departureDate, int adults) throws JsonProcessingException {
        FlightOfferResponseDto flightOffers = amadeusApiClient.fetchFlightOffers(originLocationCode, destinationLocationCode, departureDate, adults);

        SearchHistory historyEntry = new SearchHistory();
        historyEntry.setOriginLocationCode(originLocationCode);
        historyEntry.setDestinationLocationCode(destinationLocationCode);
        historyEntry.setDepartureDate(departureDate);
        historyEntry.setAdults(adults);
        historyEntry.setSearchTimestamp(LocalDateTime.now());
        searchHistoryRepository.save(historyEntry);

        return flightOffers;
    }

    public Page<SearchHistory> getSearchHistory(int page, int size) {
        System.out.println("Pobieranie historii wyszukiwań z bazy danych - strona: " + page + ", rozmiar: " + size);
        Pageable pageable = PageRequest.of(page, size);
        return searchHistoryRepository.findAll(pageable);
    }
}