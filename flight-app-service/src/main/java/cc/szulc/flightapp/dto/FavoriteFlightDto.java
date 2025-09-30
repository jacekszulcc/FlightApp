package cc.szulc.flightapp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteFlightDto {
    private Long id;
    private String origin;
    private String destination;
    private String departureDate;
    private String arrivalDate;
    private String carrier;
    private BigDecimal price;
    private LocalDateTime addedAt;
}
