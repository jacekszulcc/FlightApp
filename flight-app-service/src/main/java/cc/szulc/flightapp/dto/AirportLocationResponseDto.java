package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirportLocationResponseDto {
    private List<AirportLocationDto> data;
}
