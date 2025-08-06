package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DepartureArrivalDto {

    @JsonProperty("iataCode")
    private String iataCode;

    @JsonProperty("terminal")
    private String terminal;

    @JsonProperty("at")
    private String at;
}
