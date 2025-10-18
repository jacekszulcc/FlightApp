package cc.szulc.flightapp.service;

import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteFlightServiceTest {

    @Mock
    private FavoriteFlightRepository favoriteFlightRepository;

    @InjectMocks
    private FavoriteFlightService favoriteFlightService;

    @Test
    void addFavorite_shouldSetAddedAtAndSaveFlight() {
        FavoriteFlight flight = new FavoriteFlight();
        flight.setOrigin("WAW");
        flight.setDestination("JFK");

        when(favoriteFlightRepository.save(any(FavoriteFlight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FavoriteFlight savedFlight = favoriteFlightService.addFavorite(flight);

        ArgumentCaptor<FavoriteFlight> flightCaptor = ArgumentCaptor.forClass(FavoriteFlight.class);
        verify(favoriteFlightRepository, times(1)).save(flightCaptor.capture());

        assertThat(flightCaptor.getValue().getAddedAt()).isNotNull();

        assertThat(savedFlight).isNotNull();
        assertThat(savedFlight.getAddedAt()).isNotNull();
        assertThat(savedFlight.getOrigin()).isEqualTo("WAW");
    }

    @Test
    void getAllFavorites_shouldReturnPageOfFlights() {
        // Given
        int page = 0;
        int size = 10;
        Pageable expectedPageable = PageRequest.of(page, size);

        FavoriteFlight flight = new FavoriteFlight();
        flight.setId(1L);
        List<FavoriteFlight> flights = Collections.singletonList(flight);
        Page<FavoriteFlight> flightPage = new PageImpl<>(flights, expectedPageable, flights.size());

        when(favoriteFlightRepository.findAll(expectedPageable)).thenReturn(flightPage);

        Page<FavoriteFlight> result = favoriteFlightService.getAllFavorites(page, size);

        verify(favoriteFlightRepository, times(1)).findAll(expectedPageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void deleteFavorite_shouldCallDeleteById() {
        Long flightId = 123L;

        doNothing().when(favoriteFlightRepository).deleteById(flightId);

        favoriteFlightService.deleteFavorite(flightId);
        
        verify(favoriteFlightRepository, times(1)).deleteById(flightId);
    }
}