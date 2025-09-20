package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SegmentDto {
    @JsonProperty("departure")
    private DepartureArrivalDto departure;

    @JsonProperty("arrival")
    private DepartureArrivalDto arrival;

    @JsonProperty("carrierCode")
    private String carrierCode;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("aircraft")
    private AircraftDto aircraft;
}
