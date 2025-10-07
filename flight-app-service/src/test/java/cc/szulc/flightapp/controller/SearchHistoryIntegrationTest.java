package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.repository.SearchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchHistoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @BeforeEach
    void cleanup() {
        searchHistoryRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void shouldReturnSavedSearchHistory() throws Exception {
        SearchHistory testEntry = new SearchHistory();
        testEntry.setOriginLocationCode("WAW");
        testEntry.setDestinationLocationCode("JFK");
        testEntry.setDepartureDate("2025-12-01");
        testEntry.setAdults(2);
        testEntry.setSearchTimestamp(LocalDateTime.now());
        searchHistoryRepository.save(testEntry);

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].originLocationCode").value("WAW"));
    }
}