package cc.szulc.flightapp.service;

import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteFlightService {

    private final FavoriteFlightRepository favoriteFlightRepository;

    public FavoriteFlight addFavorite(FavoriteFlight favoriteFlight) {
        favoriteFlight.setAddedAt(LocalDateTime.now());
        return favoriteFlightRepository.save(favoriteFlight);
    }

    public List<FavoriteFlight> getAllFavorites() {
        return favoriteFlightRepository.findAll();
    }

    public void deleteFavorite(Long id) {
        favoriteFlightRepository.deleteById(id);
    }
}
