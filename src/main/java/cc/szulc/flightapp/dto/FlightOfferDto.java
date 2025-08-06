package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FlightOfferDto {

    @JsonProperty("type")
    private String type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("source")
    private String source;

    @JsonProperty("instantTicketingRequired")
    private Boolean instantTicketingRequired;

    @JsonProperty("nonHomogeneous")
    private Boolean nonHomogeneous;

    @JsonProperty("oneWay")
    private Boolean oneWay;

    @JsonProperty("lastTicketingDate")
    private String lastTicketingDate;

    @JsonProperty("numberOfBookableSeats")
    private int numberOfBookableSeats;

    @JsonProperty("price")
    private PriceDto price;

    @JsonProperty("itineraries")
    private List<ItineraryDto> itineraries;
}
