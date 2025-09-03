package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.dto.SearchHistoryDto;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.service.FlightSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FlightController {

    private final FlightSearchService flightSearchService;

    public FlightController(FlightSearchService flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    @GetMapping("/flights")
    public FlightOfferResponseDto findFlights(
        @RequestParam("originLocationCode") String origin,
        @RequestParam("destinationLocationCode") String destination,
        @RequestParam("departureDate") String date,
        @RequestParam("adults") int adults
    )  throws JsonProcessingException {
        return flightSearchService.searchForFlights(origin, destination, date, adults);
    }

    @GetMapping("/history")
    public Page<SearchHistoryDto> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        Page<SearchHistory> historyPage = flightSearchService.getSearchHistory(page, size);
        return historyPage.map(flightSearchService::mapToDto);
    }

}