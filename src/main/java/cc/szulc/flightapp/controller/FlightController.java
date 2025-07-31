package cc.szulc.flightapp.controller;

import cc.szulc.flightapp.service.FlightSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightSearchService flightSearchService;

    public FlightController(FlightSearchService flightSearchService) {
        this.flightSearchService = flightSearchService;
    }

    @GetMapping
    public String findFlights(){
        return flightSearchService.searchForFlights();
    }

}
