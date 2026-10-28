package com.travelplanner.demo;

import com.travelplanner.booking.BookingFacade;
import com.travelplanner.disruption.Disruption;
import com.travelplanner.disruption.ReplanningService;
import com.travelplanner.domain.*;
import com.travelplanner.notification.NotificationService;
import com.travelplanner.planner.TripPlanner;
import com.travelplanner.provider.MockCabProviderAdapter;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockHotelProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;
import com.travelplanner.ranking.BalancedStrategy;
import com.travelplanner.ranking.CheapestStrategy;
import com.travelplanner.ranking.FastestStrategy;
import com.travelplanner.repository.BookingRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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
                        List.of("FASTEST")
                );

        List<Itinerary> itineraries = tripPlanner.search(request);

        List<String> preferences = request.getPreferences();

        String preference = preferences == null || preferences.isEmpty() ? "BALANCED" : preferences.get(0);

        if (preferences == null) {
            throw new IllegalArgumentException("Ranking preferences cannot be null");
        }

        preference = preference.trim().toUpperCase(Locale.ROOT);

        switch (preference) {
            case "CHEAPEST" ->
                tripPlanner.setRankingStrategy(new CheapestStrategy());
            case "FASTEST" ->
                tripPlanner.setRankingStrategy(new FastestStrategy());
            case "BALANCED" ->
                tripPlanner.setRankingStrategy(new BalancedStrategy());
            default ->
                throw new IllegalArgumentException(
                        "Unsupported ranking preference: " + preference
                );
        }



        List<Itinerary> rankedItineraries = tripPlanner.rankItineraries(itineraries);

        System.out.println();
        System.out.println(
                "Ranking Strategy: " + preference
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

        System.out.println();
        System.out.println("=== Booking Storage ===");

        BookingRepository bookingRepository = new BookingRepository();

        bookingRepository.save(booking);

        System.out.println(
                "Booking saved with ID: "
                        + booking.getBookingId()
        );

        bookingRepository
                .findById(
                        booking.getBookingId()
                )
                        .ifPresentOrElse(
                                storedBooking -> {
                                    System.out.println(
                                            "Stored booking found."
                                    );

                                    System.out.println(
                                            "Booking ID: "
                                                    + storedBooking.getBookingId()
                                    );

                                    System.out.println(
                                            "Status: "
                                                    + storedBooking.getStatus()
                                    );
                                },
                                () -> {
                                    System.out.println("Stored booking not found.");
                                }
                        );

        System.out.println();
        System.out.println(
                "=== Separate Disruption Scenario ==="
        );

        System.out.println(
                "Demonstrating disruption handling on an itinerary containing flight AI777."
        );

        Itinerary disruptedItinerary = null;
        FlightSegment disruptedFlight = null;

        for (Itinerary itinerary : rankedItineraries) {
            for (TravelSegment segment : itinerary.getSegments()) {
                if (segment instanceof FlightSegment flight
                    && flight.getFlightNumber()
                        .equalsIgnoreCase("AI777"))
                {
                    disruptedItinerary = itinerary;
                    disruptedFlight = flight;
                    break;
                }
            }

            if (disruptedFlight != null) {
                break;
            }
        }
        if (disruptedFlight != null) {
            System.out.println();
            System.out.println(
                    "=== Affected Itinerary ==="
            );

            printItinerary(disruptedItinerary);

            System.out.println();

            System.out.println(
                    "Disrupted flight: "
                            + disruptedFlight.getFlightNumber()
                            + " | "
                            + disruptedFlight.getOrigin()
                            + " -> "
                            + disruptedFlight.getDestination()
            );

            Disruption disruption = new Disruption(disruptedFlight, "Flight Cancelled");
            ReplanningService replanningService = new ReplanningService(tripPlanner);

            List<Itinerary> alternatives = replanningService.replan(request, disruption);

            System.out.println(
                    "Replacement itineraries found: "
                            + alternatives.size()
            );

            Itinerary replacementItinerary = null;

            if (!alternatives.isEmpty()){
                replacementItinerary = alternatives.get(0);
                System.out.println();
                System.out.println(
                        "=== Replacement For Remaining Journey ==="
                );
                printItinerary(
                        replacementItinerary
                );
            }

            NotificationService notificationService = new NotificationService();

            Notification notification = notificationService.createDisruptionNotification(
                    disruption,
                    replacementItinerary
            );

            System.out.println();
            System.out.println(
                    "=== Traveler Notification ==="
            );

            System.out.println(
                    "Type: "
                            + notification.getType()
            );

            System.out.println(
                    notification.getMessage()
            );
        } else {
            System.out.println(
                    "No AI777 flight found for disruption demo."
            );
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
