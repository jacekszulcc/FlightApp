package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.entity.Role;
import cc.szulc.flightapp.entity.SearchHistory;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.repository.SearchHistoryRepository;
import cc.szulc.flightapp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USERNAME = "historyUser";

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();

        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(Role.ROLE_USER);
        User savedUser = userRepository.save(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(savedUser, null, savedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void cleanup() {
        searchHistoryRepository.deleteAll();
        userRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnSavedSearchHistory() throws Exception {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SearchHistory testEntry = new SearchHistory();
        testEntry.setOriginLocationCode("WAW");
        testEntry.setDestinationLocationCode("JFK");
        testEntry.setDepartureDate("2025-12-01");
        testEntry.setAdults(2);
        testEntry.setSearchTimestamp(LocalDateTime.now());
        testEntry.setUser(currentUser);
        searchHistoryRepository.save(testEntry);

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].originLocationCode").value("WAW"));
    }
}