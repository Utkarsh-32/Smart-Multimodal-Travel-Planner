package com.travelplanner.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TripRequest {
    private String origin;
    private String destination;

    private LocalDate departureDate;
    private LocalDate returnDate;

    private BigDecimal budget;

    private List<String> preferences;

    public TripRequest(
            String origin,
            String destination,
            LocalDate departureDate,
            LocalDate returnDate,
            BigDecimal budget,
            List<String> preferences) {

        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.budget = budget;
        this.preferences = preferences;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public List<String> getPreferences() {
        return preferences;
    }
}
