package com.travelplanner.demo;

import com.travelplanner.disruption.Disruption;
import com.travelplanner.disruption.ReplanningService;
import com.travelplanner.domain.FlightSegment;
import com.travelplanner.domain.Itinerary;
import com.travelplanner.domain.TravelSegment;
import com.travelplanner.domain.TripRequest;
import com.travelplanner.planner.TripPlanner;
import com.travelplanner.provider.MockCabProviderAdapter;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DisruptionDemo {
    public static void main(String[] args) {
        MockFlightProviderAdapter flightProvider = new MockFlightProviderAdapter();

        MockTrainProviderAdapter trainProvider = new MockTrainProviderAdapter();

        MockCabProviderAdapter cabProvider = new MockCabProviderAdapter();

        TripPlanner tripPlanner = new TripPlanner(
                flightProvider,
                trainProvider,
                cabProvider,
                60
        );

        TripRequest originalRequest =
                new TripRequest(
                        "Delhi",
                        "Mumbai",
                        LocalDate.of(2026, 11, 10),
                        null,
                        new BigDecimal("10000"),
                        List.of("BALANCED")
                );

        FlightSegment disruptedFlight =
                new FlightSegment(
                        "Ajmer",
                        "Mumbai",
                        LocalDateTime.of(2026, 11, 10, 15, 0),
                        LocalDateTime.of(2026, 11, 10, 16, 30),
                        new BigDecimal("3200"),
                        "AI777",
                        "Air India"
                );

        Disruption disruption = new Disruption(
                disruptedFlight,
                "Flight Cancelled"
        );

        ReplanningService replanningService = new ReplanningService(
                tripPlanner
        );

        List<Itinerary> alternatives = replanningService.replan(
                originalRequest,
                disruption
        );

        System.out.println("Disruption reason: " + disruption.getReason());

        System.out.println("Alternative itineraries found: " + alternatives.size());

        for (Itinerary itinerary : alternatives) {
            System.out.println("\nAlternative Itinerary:");

            for (TravelSegment segment : itinerary.getSegments()) {
                if (segment instanceof FlightSegment flight) {

                    System.out.println(
                            flight.getOrigin()
                                    + " -> "
                                    + flight.getDestination()
                                    + " | Flight "
                                    + flight.getFlightNumber()
                                    + " | "
                                    + flight.getAirline()
                                    + " | "
                                    + flight.getDepartureTime()
                                    + " -> "
                                    + flight.getArrivalTime()
                    );

                } else {

                    System.out.println(
                            segment.getOrigin()
                                    + " -> "
                                    + segment.getDestination()
                                    + " | "
                                    + segment.getClass().getSimpleName()
                                    + " | "
                                    + segment.getDepartureTime()
                                    + " -> "
                                    + segment.getArrivalTime()
                    );
                }
            }
        }

    }
}
