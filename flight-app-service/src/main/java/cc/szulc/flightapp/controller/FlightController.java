package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.*;
import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.service.FavoriteFlightService;
import cc.szulc.flightapp.service.FlightSearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class FlightController {

    private final FlightSearchService flightSearchService;
    private final FavoriteFlightService favoriteFlightService;

    public FlightController(FlightSearchService flightSearchService, FavoriteFlightService favoriteFlightService) {
        this.flightSearchService = flightSearchService;
        this.favoriteFlightService = favoriteFlightService;
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

    @GetMapping("/locations")
    public AirportLocationResponseDto findLocations(
            @RequestParam @NotBlank @Size(min = 1, max = 50) String keyword
    ) throws JsonProcessingException {
        return flightSearchService.searchAirports(keyword);
    }

    @GetMapping("/history")
    public Page<SearchHistoryDto> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        Page<SearchHistory> historyPage = flightSearchService.getSearchHistory(page, size);
        return historyPage.map(this::mapToDto);
    }


    @PostMapping("/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteFlightDto addFavorite(@RequestBody CreateFavoriteFlightRequestDto request) {
        FavoriteFlight favoriteFlight = mapToEntity(request);
        FavoriteFlight savedFavorite = favoriteFlightService.addFavorite(favoriteFlight);
        return mapToDto(savedFavorite);
    }

    @GetMapping("/favorites")
    public Page<FavoriteFlightDto> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<FavoriteFlight> favoritesPage = favoriteFlightService.getAllFavorites(page, size);
        return favoritesPage.map(this::mapToDto);
    }

    @DeleteMapping("/favorites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFavorite(@PathVariable Long id) {
        favoriteFlightService.deleteFavorite(id);
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

    private FavoriteFlightDto mapToDto(FavoriteFlight entity) {
        FavoriteFlightDto dto = new FavoriteFlightDto();
        dto.setId(entity.getId());
        dto.setOrigin(entity.getOrigin());
        dto.setDestination(entity.getDestination());
        dto.setDepartureDate(entity.getDepartureDate());
        dto.setArrivalDate(entity.getArrivalDate());
        dto.setCarrier(entity.getCarrier());
        dto.setPrice(entity.getPrice());
        dto.setAddedAt(entity.getAddedAt());
        return dto;
    }

    private FavoriteFlight mapToEntity(CreateFavoriteFlightRequestDto dto) {
        FavoriteFlight entity = new FavoriteFlight();
        entity.setOrigin(dto.getOrigin());
        entity.setDestination(dto.getDestination());
        entity.setDepartureDate(dto.getDepartureDate());
        entity.setArrivalDate(dto.getArrivalDate());
        entity.setCarrier(dto.getCarrier());
        entity.setPrice(dto.getPrice());
        return entity;
    }
}