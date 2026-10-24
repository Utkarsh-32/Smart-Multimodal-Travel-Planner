package com.travelplanner.provider;

import com.travelplanner.domain.FlightSegment;
import com.travelplanner.domain.ProviderAdapter;
import com.travelplanner.domain.TripRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockFlightProviderAdapter implements ProviderAdapter<FlightSegment> {

    private boolean reservationShouldSucceed;
    public MockFlightProviderAdapter() {
        this.reservationShouldSucceed = true;
    }

    public MockFlightProviderAdapter(
            boolean reservationShouldSucceed
    ) {
        this.reservationShouldSucceed = reservationShouldSucceed;
    }

    @Override
    public List<FlightSegment> search(TripRequest request) {
        List<FlightSegment> flights = new ArrayList<>();

        FlightSegment flight1 = new FlightSegment(
                "Jaipur",
                "Mumbai",
                LocalDateTime.of(2026, 11, 10, 12, 0),
                LocalDateTime.of(2026, 11, 10, 14, 10),
                new BigDecimal("3500"),
                "AI302",
                "Air India"
        );

        FlightSegment flight2 = new FlightSegment(
                "Delhi",
                "Mumbai",
                LocalDateTime.of(2026, 11, 10, 14, 30),
                LocalDateTime.of(2026, 11, 10, 16, 40),
                new BigDecimal("3900"),
                "6E512",
                "IndiGo"
        );

        FlightSegment flight3 = new FlightSegment(
                "Ajmer",
                "Mumbai",
                LocalDateTime.of(2026, 11, 10, 15, 0),
                LocalDateTime.of(2026, 11, 10, 16, 30),
                new BigDecimal("3200"),
                "AI777",
                "Air India"
        );

        FlightSegment flight4 = new FlightSegment(
                "Ajmer",
                "Mumbai",
                LocalDateTime.of(2026, 11, 10, 17, 30),
                LocalDateTime.of(2026, 11, 10, 19, 0),
                new BigDecimal("3600"),
                "6E999",
                "IndiGo"
        );

        flights.add(flight1);
        flights.add(flight2);
        flights.add(flight3);
        flights.add(flight4);

        return flights;
    }

    @Override
    public boolean reserve(FlightSegment option) {
        return reservationShouldSucceed;
    }

    @Override
    public boolean cancel(FlightSegment option) {
        return true;
    }

};
