package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AircraftDto {

    @JsonProperty("code")
    private String code;
}
