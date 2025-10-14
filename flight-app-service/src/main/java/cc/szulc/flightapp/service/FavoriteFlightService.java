package cc.szulc.flightapp.service;

import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FavoriteFlightService {

    private final FavoriteFlightRepository favoriteFlightRepository;

    public FavoriteFlight addFavorite(FavoriteFlight favoriteFlight) {
        favoriteFlight.setAddedAt(LocalDateTime.now());
        return favoriteFlightRepository.save(favoriteFlight);
    }

    public Page<FavoriteFlight> getAllFavorites(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return favoriteFlightRepository.findAll(pageable);
    }

    public void deleteFavorite(Long id) {
        favoriteFlightRepository.deleteById(id);
    }
}