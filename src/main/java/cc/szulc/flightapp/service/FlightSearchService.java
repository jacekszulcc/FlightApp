package cc.szulc.flightapp.service;

import cc.szulc.flightapp.client.AmadeusApiClient;
import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.repository.SearchHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
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
        log.info("Pobieranie historii wyszukiwań z bazy danych - strona: {}, rozmiar: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return searchHistoryRepository.findAll(pageable);
    }
}