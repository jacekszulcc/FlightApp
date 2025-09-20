package cc.szulc.flightapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class FlightOfferResponseDto {
    private List<FlightOfferDto> data;
}
