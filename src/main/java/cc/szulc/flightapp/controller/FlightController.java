package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.dto.SearchHistoryDto;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.service.FlightSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
public class FlightController {

    private final FlightSearchService flightSearchService;

    public FlightController(FlightSearchService flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    @GetMapping("/flights")
    public FlightOfferResponseDto findFlights(
            @RequestParam @NotBlank @Size(min = 3, max = 3) String originLocationCode,
            @RequestParam @NotBlank @Size(min = 3, max = 3) String destinationLocationCode,
            @RequestParam @NotBlank String departureDate,
            @RequestParam @NotNull @Positive int adults
    ) throws JsonProcessingException {
        return flightSearchService.searchForFlights(originLocationCode, destinationLocationCode, departureDate, adults);
    }

    @GetMapping("/history")
    public Page<SearchHistoryDto> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        Page<SearchHistory> historyPage = flightSearchService.getSearchHistory(page, size);
        return historyPage.map(this::mapToDto);
    }

    private SearchHistoryDto mapToDto(SearchHistory entity) {
        SearchHistoryDto dto = new SearchHistoryDto();
        dto.setId(entity.getId());
        dto.setOriginLocationCode(entity.getOriginLocationCode());
        dto.setDestinationLocationCode(entity.getDestinationLocationCode());
        dto.setDepartureDate(entity.getDepartureDate());
        dto.setAdults(entity.getAdults());
        dto.setSearchTimestamp(entity.getSearchTimestamp());
        return dto;
    }
}