package cc.szulc.flightapp.repository;

import cc.szulc.flightapp.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // "unless" keeps empty results out of the cache. Without it a lookup made
    // before a user is created (as DataInitializer does on startup) would hide
    // that user until the cache entry expires.
    @Cacheable(value = "users", unless = "#result == null")
    Optional<User> findByUsername(String username);
}