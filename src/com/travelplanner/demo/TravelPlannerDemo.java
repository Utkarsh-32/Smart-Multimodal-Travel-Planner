package com.travelplanner.demo;

import com.travelplanner.domain.*;
import com.travelplanner.planner.TripPlanner;
import com.travelplanner.provider.MockCabProviderAdapter;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockHotelProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TravelPlannerDemo {
    public static void main(String[] args) {
        System.out.println(
                "=== Smart Multimodal Travel Planner ==="
        );

        MockFlightProviderAdapter flightProvider =
                new MockFlightProviderAdapter();

        MockTrainProviderAdapter trainProvider =
                new MockTrainProviderAdapter();

        MockCabProviderAdapter cabProvider =
                new MockCabProviderAdapter();

        MockHotelProviderAdapter hotelProvider =
                new MockHotelProviderAdapter();

        TripPlanner tripPlanner =
                new TripPlanner(
                        flightProvider,
                        trainProvider,
                        cabProvider,
                        hotelProvider,
                        60
                );

        TripRequest request =
                new TripRequest(
                        "Delhi",
                        "Mumbai",
                        LocalDate.of(2026, 11, 10),
                        null,
                        new BigDecimal("10000"),
                        List.of("BALANCED")
                );

        List<Itinerary> itineraries = tripPlanner.search(request);

        System.out.println();
        System.out.println(
                "Itineraries found: "
                        + itineraries.size()
        );

        for (int i = 0; i < itineraries.size(); i++) {
            System.out.println();
            System.out.println(
                    "----- Itinerary "
                            + (i + 1)
                            + " -----"
            );
            printItinerary(itineraries.get(i));
        }
    }

    private static void printItinerary(Itinerary itinerary) {
        for (TravelSegment segment : itinerary.getSegments()) {
            if (segment instanceof FlightSegment flight) {
                System.out.println(
                        "FLIGHT: "
                                + flight.getOrigin()
                                + " -> "
                                + flight.getDestination()
                                + " | "
                                + flight.getFlightNumber()
                                + " | "
                                + flight.getAirline()
                                +  " | "
                                + "Departure: "
                                + flight.getDepartureTime()
                                + " | "
                                + "Arrival: "
                                + flight.getArrivalTime()
                );
            } else if (segment instanceof TrainSegment train) {
                System.out.println(
                        "TRAIN: "
                                + train.getOrigin()
                                + " -> "
                                + train.getDestination()
                                + " | "
                                + train.getTrainNumber()
                                + " | "
                                + train.getTrainName()
                                + " | "
                                + "Departure: "
                                + train.getDepartureTime()
                                + " | "
                                + "Arrival: "
                                + train.getArrivalTime()
                );
            } else if (segment instanceof CabSegment cab) {

                System.out.println(
                        "CAB: "
                                + cab.getOrigin()
                                + " -> "
                                + cab.getDestination()
                                + " | "
                                + cab.getProvider()
                                + " | "
                                + cab.getVehicleType()
                                + " | "
                                + cab.getVehicleNumber()
                                + " | "
                                + "Departure: "
                                + cab.getDepartureTime()
                                + " | Arrival: "
                                + cab.getArrivalTime()
                );
            }
        }

        if (itinerary.getHotelReservation() != null) {
            HotelReservation hotel = itinerary.getHotelReservation();

            System.out.println(
                    "HOTEL: "
                            + hotel.getHotelName()
                            + " | "
                            + hotel.getLocation()
                            + " | "
                            + hotel.getCheckInDate()
                            + " -> "
                            + hotel.getCheckOutDate()
            );
        }

        System.out.println(
                "Total Cost: "
                        + itinerary.getTotalCost()
        );

        System.out.println(
                "Total Duration: "
                        + itinerary.getTotalDurationMinutes()
                        + " minutes"
        );

        System.out.println(
                "Transfers: "
                        + itinerary.getNumberOfTransfers()
        );
    }
}
