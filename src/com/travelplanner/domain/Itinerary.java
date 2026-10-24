package com.travelplanner.domain;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Itinerary {

    private List<TravelSegment> segments;

    private HotelReservation hotelReservation;

    private BigDecimal totalCost;

    private long totalDurationMinutes;

    public Itinerary() {
        this.segments = new ArrayList<>();
        this.totalCost = BigDecimal.ZERO;
        this.totalDurationMinutes = 0;
    }

    public void addSegment(TravelSegment segment) {
        segments.add(segment);
        recalculateTotals();
    }

    private void recalculateTotals() {
        totalCost = BigDecimal.ZERO;

        for (TravelSegment segment : segments) {
            totalCost = totalCost.add(segment.getPrice());
        }

        if (!segments.isEmpty()) {
            TravelSegment firstSegment = segments.get(0);
            TravelSegment lastSegment = segments.get(segments.size() - 1);
            totalDurationMinutes =
                    ChronoUnit.MINUTES.between(
                            firstSegment.getDepartureTime(),
                            lastSegment.getArrivalTime()
                    );
        }
    }

    public List<TravelSegment>  getSegments() {
        return segments;
    }

    public HotelReservation getHotelReservation() {
        return hotelReservation;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public long getTotalDurationMinutes() {
        return totalDurationMinutes;
    }
}
