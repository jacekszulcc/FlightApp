package cc.szulc.flightapp.repository;

import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    Page<SearchHistory> findAllByUser(User user, Pageable pageable);
}