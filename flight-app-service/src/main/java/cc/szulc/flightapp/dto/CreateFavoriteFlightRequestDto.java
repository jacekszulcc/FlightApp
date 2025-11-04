package cc.szulc.flightapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateFavoriteFlightRequestDto {
    @NotBlank(message = "Origin cannot be blank")
    @Size(min = 3, max = 50, message = "Origin must be between 3 and 50 characters")
    private String origin;

    @NotBlank(message = "Destination cannot be blank")
    @Size(min = 3, max = 50, message = "Destination must be between 3 and 50 characters")
    private String destination;

    @NotBlank(message = "Departure date cannot be blank")
    private String departureDate;

    @NotBlank(message = "Arrival date cannot be blank")
    private String arrivalDate;

    @NotBlank(message = "Carrier cannot be blank")
    private String carrier;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;
}
