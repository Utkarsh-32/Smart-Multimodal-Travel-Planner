package com.travelplanner.demo;
import com.travelplanner.domain.Itinerary;
import com.travelplanner.domain.TripRequest;
import com.travelplanner.planner.TripPlanner;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProviderDemo {
    public static void main(String[] args) {
        TripRequest request = new TripRequest(
                "Delhi",
                "Mumbai",
                LocalDate.of(2026,11,10),
                null,
                new BigDecimal("10000"),
                List.of("CHEAPEST")
        );

        MockFlightProviderAdapter flightProvider = new MockFlightProviderAdapter();

        MockTrainProviderAdapter trainProvider = new MockTrainProviderAdapter();

        TripPlanner tripPlanner = new TripPlanner(flightProvider, trainProvider, 60);

        List<Itinerary> itineraries = tripPlanner.search(request);

        System.out.println("Itineraries generated " + itineraries.size());

        for (int i = 0; i < itineraries.size(); i++) {

            Itinerary itinerary =
                    itineraries.get(i);

            System.out.println();
            System.out.println(
                    "Itinerary " + (i + 1)
            );

            System.out.println(
                    "Total cost: "
                            + itinerary.getTotalCost()
            );

            System.out.println(
                    "Total duration: "
                            + itinerary.getTotalDurationMinutes()
                            + " minutes"
            );

            System.out.println(
                    "Transfers: "
                            + itinerary.getNumberOfTransfers()
            );

            if (itinerary.getHotelReservation() != null) {

                System.out.println(
                        "Hotel: "
                                + itinerary
                                .getHotelReservation()
                                .getHotelName()
                );

                System.out.println(
                        "Hotel price: "
                                + itinerary
                                .getHotelReservation()
                                .getPrice()
                );

            } else {

                System.out.println(
                        "Hotel: none"
                );
            }
        }
    }
}
