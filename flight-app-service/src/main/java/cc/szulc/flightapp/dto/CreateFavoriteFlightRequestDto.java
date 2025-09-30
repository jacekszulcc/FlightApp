package cc.szulc.flightapp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateFavoriteFlightRequestDto {
    private String origin;
    private String destination;
    private String departureDate;
    private String arrivalDate;
    private String carrier;
    private BigDecimal price;
}
