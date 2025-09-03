package cc.szulc.flightapp.repository;

import cc.szulc.flightapp.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository; // <-- ZMIANA TUTAJ
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    // Interfejs pozostaje pusty w środku
}