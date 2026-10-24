package com.travelplanner.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlightSegment extends TravelSegment {
    private String flightNumber;
    private String airline;

    public FlightSegment(
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            BigDecimal price,
            String flightNumber,
            String airline
    ) {
        super (
                origin,
                destination,
                departureTime,
                arrivalTime,
                price
        );

        this.flightNumber = flightNumber;
        this.airline = airline;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getAirline() {
        return airline;
    }

}
