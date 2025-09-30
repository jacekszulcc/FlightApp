package cc.szulc.flightapp.repository;

import cc.szulc.flightapp.entity.FavoriteFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteFlightRepository extends JpaRepository<FavoriteFlight, Long> {
}