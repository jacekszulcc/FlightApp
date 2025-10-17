package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.CreateFavoriteFlightRequestDto;
import cc.szulc.flightapp.repository.FavoriteFlightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

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
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanup() {
        favoriteFlightRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void shouldAddAndRetrieveFavoriteFlight() throws Exception {
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

        assertThat(favoriteFlightRepository.findById(favoriteId)).isEmpty();
    }
}