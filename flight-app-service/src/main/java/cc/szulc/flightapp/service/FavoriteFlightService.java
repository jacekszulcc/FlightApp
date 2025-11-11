package cc.szulc.flightapp.service;

import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FavoriteFlightService {

    private final FavoriteFlightRepository favoriteFlightRepository;

    public FavoriteFlight addFavorite(FavoriteFlight favoriteFlight) {
        User currentUser = getCurrentUser();
        favoriteFlight.setUser(currentUser);
        favoriteFlight.setAddedAt(LocalDateTime.now());
        return favoriteFlightRepository.save(favoriteFlight);
    }

    public Page<FavoriteFlight> getAllFavorites(int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return favoriteFlightRepository.findAllByUser(currentUser, pageable);
    }

    public void deleteFavorite(Long id) {
        User currentUser = getCurrentUser();
        FavoriteFlight flight = favoriteFlightRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono ulubionego lotu o ID: " + id + " lub nie należy on do Ciebie."));

        favoriteFlightRepository.deleteById(flight.getId());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Użytkownik nie jest zalogowany lub nie można go zidentyfikować.");
        }
        return (User) authentication.getPrincipal();
    }
}