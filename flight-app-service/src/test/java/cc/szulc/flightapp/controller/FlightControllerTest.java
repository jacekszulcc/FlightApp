package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.dto.FlightOfferResponseDto;
import cc.szulc.flightapp.service.FlightSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FlightControllerTest {
    @MockBean
    private FlightSearchService flightSearchService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldReturnFlightWhenCorrectParametersAreProvided() throws Exception{
        FlightOfferResponseDto testResponse = new FlightOfferResponseDto();

        String futureDate = LocalDate.now().plusDays(1).toString();

        when(flightSearchService.searchForFlights(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(testResponse);

        MvcResult mvcResult = mockMvc.perform(get("/api/flights")
                        .param("originLocationCode", "MAD")
                        .param("destinationLocationCode", "JFK")
                        .param("departureDate", futureDate) // Użycie zmiennej
                        .param("adults", "1"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        FlightOfferResponseDto actualResponse = objectMapper.readValue(responseBodyAsString, FlightOfferResponseDto.class);

        assertThat(actualResponse).isEqualTo(testResponse);
    }
}