package cc.szulc.flightapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceDto {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("base")
    private BigDecimal base;
}
