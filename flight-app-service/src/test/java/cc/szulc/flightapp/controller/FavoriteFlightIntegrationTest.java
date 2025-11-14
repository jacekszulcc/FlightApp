package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.CreateFavoriteFlightRequestDto;
import cc.szulc.flightapp.entity.FavoriteFlight;
import cc.szulc.flightapp.entity.Role;
import cc.szulc.flightapp.entity.User;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import cc.szulc.flightapp.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FavoriteFlightIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavoriteFlightRepository favoriteFlightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TEST_USERNAME = "testuser";

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
        favoriteFlightRepository.deleteAll();
        userRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAddAndSoftDeleteFavoriteFlight() throws Exception {
        CreateFavoriteFlightRequestDto requestDto = new CreateFavoriteFlightRequestDto();
        requestDto.setOrigin("GDN");
        requestDto.setDestination("KRK");
        requestDto.setDepartureDate("2025-10-01T08:00:00");
        requestDto.setArrivalDate("2025-10-01T09:00:00");
        requestDto.setCarrier("Ryanair");
        requestDto.setPrice(new BigDecimal("199.99"));

        String responseAsString = mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long favoriteId = objectMapper.readTree(responseAsString).get("id").asLong();

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].destination").value("KRK"));

        mockMvc.perform(delete("/api/favorites/" + favoriteId))
                .andExpect(status().isNoContent());

        Optional<FavoriteFlight> deletedFlightOptional = favoriteFlightRepository.findById(favoriteId);
        assertThat(deletedFlightOptional).isPresent();
        assertThat(deletedFlightOptional.get().isDeleted()).isTrue();

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}