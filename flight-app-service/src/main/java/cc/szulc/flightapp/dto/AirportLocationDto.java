package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirportLocationDto {

    private String name;
    private String iataCode;

    @JsonProperty("address")
    private void unpackAddress(AddressDto address) {
        if (address != null) {}
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AddressDto {
        private String cityName;
        private String countryName;
    }
}
