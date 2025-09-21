package cc.szulc.flightapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SearchHistoryDto {
    private Long id;
    private String originLocationCode;
    private String destinationLocationCode;
    private String departureDate;
    private int adults;
    private LocalDateTime searchTimestamp;
}
