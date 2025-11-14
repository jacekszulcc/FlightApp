package cc.szulc.flightapp.repository;

import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteFlightRepository extends JpaRepository<FavoriteFlight, Long> {

    Page<FavoriteFlight> findAllByUserAndIsDeletedFalse(User user, Pageable pageable);

    Optional<FavoriteFlight> findByIdAndUser(Long id, User user);
}