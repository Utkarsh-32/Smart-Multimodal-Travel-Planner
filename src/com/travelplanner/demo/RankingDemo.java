package com.travelplanner.demo;

import com.travelplanner.domain.FlightSegment;
import com.travelplanner.domain.Itinerary;
import com.travelplanner.planner.TripPlanner;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;
import com.travelplanner.ranking.BalancedStrategy;
import com.travelplanner.ranking.CheapestStrategy;
import com.travelplanner.ranking.FastestStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RankingDemo {
    public static void main(String[] args) {
        Itinerary itinerary1 = new Itinerary();

        itinerary1.addSegment(
                new FlightSegment(
                        "Delhi",
                        "Mumbai",
                        LocalDateTime.of(2026,11,10,8,0),
                        LocalDateTime.of(2026,11,10,10,0),
                        new BigDecimal("5000"),
                        "F1",
                        "Airline A"
                )
        );

        Itinerary itinerary2 = new Itinerary();
        itinerary2.addSegment(
                new FlightSegment(
                        "Delhi",
                        "Mumbai",
                        LocalDateTime.of(2026, 11, 10, 9, 0),
                        LocalDateTime.of(2026, 11, 10, 12, 0),
                        new BigDecimal("2500"),
                        "F2",
                        "Airline B"
                )
        );

        Itinerary itinerary3 = new Itinerary();
        itinerary3.addSegment(
                new FlightSegment(
                        "Delhi",
                        "Mumbai",
                        LocalDateTime.of(2026, 11, 10, 7, 0),
                        LocalDateTime.of(2026, 11, 10, 11, 0),
                        new BigDecimal("3500"),
                        "F3",
                        "Airline C"
                )
        );

        List<Itinerary> candidates = new ArrayList<>();

        candidates.add(itinerary1);
        candidates.add(itinerary2);
        candidates.add(itinerary3);

        TripPlanner tripPlanner =
                new TripPlanner(
                        new MockFlightProviderAdapter(),
                        new MockTrainProviderAdapter(),
                        60
                );

        tripPlanner.setRankingStrategy(new BalancedStrategy());

        List<Itinerary> balancedRanked =
                tripPlanner.rankItineraries(candidates);

        System.out.println("Balanced ranking:");

        for (Itinerary itinerary : balancedRanked) {
            System.out.println(
                    "Cost: " + itinerary.getTotalCost()
                            + ", Duration: "
                            + itinerary.getTotalDurationMinutes()
                            + " minutes"
            );
        }

    }
}
