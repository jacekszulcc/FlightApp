package cc.szulc.flightapp.service;

import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteFlightServiceTest {

    @Mock
    private FavoriteFlightRepository favoriteFlightRepository;

    @InjectMocks
    private FavoriteFlightService favoriteFlightService;

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
    void addFavorite_shouldSetAddedAtAndSaveFlight() {
        mockAuthenticatedUser();

        FavoriteFlight flight = new FavoriteFlight();
        flight.setOrigin("WAW");
        flight.setDestination("JFK");

        when(favoriteFlightRepository.save(any(FavoriteFlight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FavoriteFlight savedFlight = favoriteFlightService.addFavorite(flight);

        ArgumentCaptor<FavoriteFlight> flightCaptor = ArgumentCaptor.forClass(FavoriteFlight.class);
        verify(favoriteFlightRepository, times(1)).save(flightCaptor.capture());

        assertThat(flightCaptor.getValue().getAddedAt()).isNotNull();
        assertThat(flightCaptor.getValue().getUser()).isEqualTo(mockUser);
        assertThat(flightCaptor.getValue().isDeleted()).isFalse();
        assertThat(savedFlight).isNotNull();
    }

    @Test
    void getAllFavorites_shouldReturnPageOfActiveFlights() {
        mockAuthenticatedUser();

        int page = 0;
        int size = 10;
        Pageable expectedPageable = PageRequest.of(page, size);

        FavoriteFlight flight = new FavoriteFlight();
        flight.setId(1L);
        List<FavoriteFlight> flights = Collections.singletonList(flight);
        Page<FavoriteFlight> flightPage = new PageImpl<>(flights, expectedPageable, flights.size());

        when(favoriteFlightRepository.findAllByUserAndIsDeletedFalse(mockUser, expectedPageable)).thenReturn(flightPage);

        Page<FavoriteFlight> result = favoriteFlightService.getAllFavorites(page, size);

        verify(favoriteFlightRepository, times(1)).findAllByUserAndIsDeletedFalse(mockUser, expectedPageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void deleteFavorite_shouldSoftDeleteFlight() {
        mockAuthenticatedUser();
        Long flightId = 123L;
        FavoriteFlight flight = new FavoriteFlight();
        flight.setId(flightId);
        flight.setUser(mockUser);
        flight.setDeleted(false);

        when(favoriteFlightRepository.findByIdAndUser(flightId, mockUser)).thenReturn(Optional.of(flight));

        favoriteFlightService.deleteFavorite(flightId);

        ArgumentCaptor<FavoriteFlight> flightCaptor = ArgumentCaptor.forClass(FavoriteFlight.class);
        verify(favoriteFlightRepository, times(1)).save(flightCaptor.capture());

        verify(favoriteFlightRepository, never()).deleteById(anyLong());

        assertThat(flightCaptor.getValue().isDeleted()).isTrue();
    }
}