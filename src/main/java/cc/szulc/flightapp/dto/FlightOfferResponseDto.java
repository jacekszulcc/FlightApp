package cc.szulc.flightapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FlightOfferResponseDto {
    private List<FlightOfferDto> data;
}
