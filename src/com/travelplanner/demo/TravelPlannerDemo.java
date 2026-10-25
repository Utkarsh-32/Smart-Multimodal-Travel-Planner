package com.travelplanner.demo;

import com.travelplanner.booking.BookingFacade;
import com.travelplanner.domain.*;
import com.travelplanner.planner.TripPlanner;
import com.travelplanner.provider.MockCabProviderAdapter;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockHotelProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;
import com.travelplanner.ranking.BalancedStrategy;

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

        tripPlanner.setRankingStrategy(new BalancedStrategy());

        List<Itinerary> rankedItineraries = tripPlanner.rankItineraries(itineraries);

        System.out.println();
        System.out.println(
                "Ranking Strategy: BALANCED"
        );

        System.out.println(
                "Itineraries found: "
                        + rankedItineraries.size()
        );

        for (int i = 0; i < rankedItineraries.size(); i++) {
            System.out.println();
            System.out.println(
                    "----- Itinerary "
                            + (i + 1)
                            + " -----"
            );
            printItinerary(rankedItineraries.get(i));
        }

        if (rankedItineraries.isEmpty()) {

            System.out.println();
            System.out.println(
                    "No itinerary available for booking."
            );

            return;
        }

        Itinerary selectedItinerary =
                rankedItineraries.get(0);

        System.out.println();
        System.out.println(
                "=== Selected Itinerary ==="
        );

        printItinerary(
                selectedItinerary
        );

        BookingFacade bookingFacade =
                new BookingFacade(
                        flightProvider,
                        trainProvider,
                        cabProvider,
                        hotelProvider
                );

        Booking booking =
                bookingFacade.book(
                        "B001",
                        selectedItinerary
                );

        System.out.println();

        System.out.println(
                "=== Booking Result ==="
        );

        System.out.println(
                "Booking ID: "
                        + booking.getBookingId()
        );

        System.out.println(
                "Booking Status: "
                        + booking.getStatus()
        );

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
