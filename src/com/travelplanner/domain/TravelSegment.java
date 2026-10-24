package com.travelplanner.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;


public abstract class TravelSegment {
    private String origin;
    private String destination;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private BigDecimal price;

    public TravelSegment(
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            BigDecimal price
    ) {
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
