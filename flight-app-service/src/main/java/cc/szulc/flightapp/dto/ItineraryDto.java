package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItineraryDto {

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("segments")
    private List<SegmentDto> segment;
}
