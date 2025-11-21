package cc.szulc.flightapp.repository;

import cc.szulc.flightapp.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable("users")
    Optional<User> findByUsername(String username);
}