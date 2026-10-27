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
import com.travelplanner.repository.BookingRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EdgeCaseDemo {
    public static void main(String[] args) {
        testNoRoute();
        System.out.println();
        testInsufficientTransferTime();
        System.out.println();
        testPartialBookingFailure();
        System.out.println();
        testCompleteBookingFailure();
        System.out.println();
        testDisruptionWithNoReplacement();
        System.out.println();
        testBookingNotFound();
    }

    private static void testNoRoute() {
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
                        "Chennai",
                        LocalDate.of(2026, 11, 10),
                        null,
                        new BigDecimal("10000"),
                        List.of("CHEAPEST")
                );

        List<Itinerary> itineraries = tripPlanner.search(request);

        System.out.println(
                "=== Test: No Available Route ==="
        );

        System.out.println(
                "Routes found: "
                        + itineraries.size()
        );

        if (itineraries.isEmpty()) {
            System.out.println("PASS: No route was returned.");
        } else {
            System.out.println("FAIL: A route was unexpectedly returned.");
        }
    }

    private static void testInsufficientTransferTime() {

        MockFlightProviderAdapter flightProvider =
                new MockFlightProviderAdapter();

        MockTrainProviderAdapter trainProvider =
                new MockTrainProviderAdapter();

        MockCabProviderAdapter cabProvider =
                new MockCabProviderAdapter();

        MockHotelProviderAdapter hotelProvider =
                new MockHotelProviderAdapter();

        long normalBuffer = 60;
        long largeBuffer = 180;

        TripRequest request =
                new TripRequest(
                        "Delhi",
                        "Mumbai",
                        LocalDate.of(2026, 11, 10),
                        null,
                        new BigDecimal("10000"),
                        List.of("CHEAPEST")
                );

        TripPlanner normalPlanner =
                new TripPlanner(
                        flightProvider,
                        trainProvider,
                        cabProvider,
                        hotelProvider,
                        normalBuffer
                );

        TripPlanner strictPlanner =
                new TripPlanner(
                        flightProvider,
                        trainProvider,
                        cabProvider,
                        hotelProvider,
                        largeBuffer
                );

        List<Itinerary> normalRoutes =
                normalPlanner.search(request);

        List<Itinerary> strictRoutes =
                strictPlanner.search(request);

        System.out.println(
                "=== Test: Insufficient Transfer Time ==="
        );

        System.out.println(
                "Routes with 60-minute buffer: "
                        + normalRoutes.size()
        );

        System.out.println(
                "Routes with 180-minute buffer: "
                        + strictRoutes.size()
        );

        boolean rejectedBecauseOfBuffer = false;

        for (Itinerary itinerary : normalRoutes) {

            List<TravelSegment> segments =
                    itinerary.getSegments();

            if (segments.size() < 2) {
                continue;
            }

            for (int i = 0;
                 i < segments.size() - 1;
                 i++) {

                TravelSegment current =
                        segments.get(i);

                TravelSegment next =
                        segments.get(i + 1);

                long actualGap =
                        java.time.Duration.between(
                                current.getArrivalTime(),
                                next.getDepartureTime()
                        ).toMinutes();


                if (actualGap >= normalBuffer
                        && actualGap < largeBuffer) {

                    rejectedBecauseOfBuffer = true;

                    System.out.println();
                    System.out.println(
                            "Affected itinerary:"
                    );

                    for (TravelSegment pathSegment : segments) {

                        System.out.println(
                                pathSegment.getOrigin()
                                        + " -> "
                                        + pathSegment.getDestination()
                                        + " | "
                                        + pathSegment.getDepartureTime()
                                        + " -> "
                                        + pathSegment.getArrivalTime()
                        );
                    }

                    System.out.println();

                    System.out.println(
                            "Affected connection:"
                    );

                    System.out.println(
                            current.getOrigin()
                                    + " -> "
                                    + current.getDestination()
                                    + " arrives at "
                                    + current.getArrivalTime()
                    );

                    System.out.println(
                            next.getOrigin()
                                    + " -> "
                                    + next.getDestination()
                                    + " departs at "
                                    + next.getDepartureTime()
                    );

                    System.out.println();

                    System.out.println(
                            "Actual transfer time: "
                                    + actualGap
                                    + " minutes"
                    );

                    System.out.println(
                            "Required transfer buffer: "
                                    + largeBuffer
                                    + " minutes"
                    );

                    System.out.println(
                            "Short by: "
                                    + (largeBuffer - actualGap)
                                    + " minutes"
                    );

                    break;
                }
            }

            if (rejectedBecauseOfBuffer) {
                break;
            }
        }

        if (rejectedBecauseOfBuffer
                && strictRoutes.size()
                < normalRoutes.size()) {

            System.out.println(
                    "PASS: Route was rejected because transfer buffer was insufficient."
            );

        } else {

            System.out.println(
                    "FAIL: No route was rejected due to the larger transfer buffer."
            );
        }
    }

    private static void testPartialBookingFailure() {
        MockFlightProviderAdapter flightProvider =
                new MockFlightProviderAdapter(true);

        MockTrainProviderAdapter trainProvider =
                new MockTrainProviderAdapter(false);

        MockCabProviderAdapter cabProvider =
                new MockCabProviderAdapter(true);

        MockHotelProviderAdapter hotelProvider =
                new MockHotelProviderAdapter(true);

        Itinerary itinerary =
                new Itinerary();

        TrainSegment train =
                new TrainSegment(
                        "Delhi",
                        "Jaipur",
                        LocalDateTime.of(2026, 11, 10, 6, 0),
                        LocalDateTime.of(2026, 11, 10, 10, 0),
                        new BigDecimal("800"),
                        "12952",
                        "Delhi Jaipur Express"
                );

        FlightSegment flight =
                new FlightSegment(
                        "Jaipur",
                        "Mumbai",
                        LocalDateTime.of(2026, 11, 10, 12, 0),
                        LocalDateTime.of(2026, 11, 10, 14, 0),
                        new BigDecimal("3500"),
                        "AI302",
                        "Air India"
                );

        itinerary.addSegment(train);
        itinerary.addSegment(flight);

        BookingFacade bookingFacade =
                new BookingFacade(
                        flightProvider,
                        trainProvider,
                        cabProvider,
                        hotelProvider
                );

        Booking booking =
                bookingFacade.book(
                        "EDGE-B001",
                        itinerary
                );

        System.out.println(
                "=== Test: Partial Booking Failure ==="
        );

        System.out.println(
                "Train reservation expected: FAILED"
        );

        System.out.println(
                "Flight reservation expected: SUCCESS"
        );

        System.out.println(
                "Final booking status: "
                        + booking.getStatus()
        );

        if (booking.getStatus()
                == BookingStatus.PARTIALLY_CONFIRMED) {

            System.out.println(
                    "PASS: Mixed reservation results produced PARTIALLY_CONFIRMED."
            );

        } else {

            System.out.println(
                    "FAIL: Expected PARTIALLY_CONFIRMED but got "
                            + booking.getStatus()
            );
        }
    }

    private static void testCompleteBookingFailure() {
        MockFlightProviderAdapter flightProvider =
                new MockFlightProviderAdapter(false);

        MockTrainProviderAdapter trainProvider =
                new MockTrainProviderAdapter(false);

        MockCabProviderAdapter cabProvider =
                new MockCabProviderAdapter(false);

        MockHotelProviderAdapter hotelProvider =
                new MockHotelProviderAdapter(false);

        Itinerary itinerary =
                new Itinerary();

        TrainSegment train =
                new TrainSegment(
                        "Delhi",
                        "Jaipur",
                        LocalDateTime.of(2026, 11, 10, 6, 0),
                        LocalDateTime.of(2026, 11, 10, 10, 0),
                        new BigDecimal("800"),
                        "12952",
                        "Delhi Jaipur Express"
                );

        FlightSegment flight =
                new FlightSegment(
                        "Jaipur",
                        "Mumbai",
                        LocalDateTime.of(2026, 11, 10, 12, 0),
                        LocalDateTime.of(2026, 11, 10, 14, 0),
                        new BigDecimal("3500"),
                        "AI302",
                        "Air India"
                );

        itinerary.addSegment(train);
        itinerary.addSegment(flight);

        BookingFacade bookingFacade = new BookingFacade(
                flightProvider,
                trainProvider,
                cabProvider,
                hotelProvider
        );

        Booking booking = bookingFacade.book(
                "EDGE-B002",
                    itinerary
        );

        System.out.println(
                "=== Test: Complete Booking Failure ==="
        );

        System.out.println(
                "Train reservation: FAILED"
        );

        System.out.println(
                "Flight reservation: FAILED"
        );

        System.out.println(
                "Final booking status: "
                        + booking.getStatus()
        );

        if (booking.getStatus()
                == BookingStatus.FAILED) {

            System.out.println(
                    "PASS: All reservation failures correctly produced FAILED."
            );

        } else {

            System.out.println(
                    "FAIL: Expected FAILED but got "
                            + booking.getStatus()
            );
        }

    }

    private static void testDisruptionWithNoReplacement() {

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

        List<Itinerary> itineraries =
                tripPlanner.search(request);

        FlightSegment disruptedFlight = null;

        for (Itinerary itinerary : itineraries) {

            for (TravelSegment segment
                    : itinerary.getSegments()) {

                if (segment instanceof FlightSegment flight
                        && flight.getFlightNumber()
                        .equalsIgnoreCase("6E999")) {

                    disruptedFlight = flight;
                    break;
                }
            }

            if (disruptedFlight != null) {
                break;
            }
        }

        System.out.println(
                "=== Test: Disruption With No Replacement ==="
        );

        if (disruptedFlight == null) {

            System.out.println(
                    "FAIL: Disruption test flight was not found."
            );

            return;
        }

        System.out.println(
                "Disrupted segment: "
                        + disruptedFlight.getOrigin()
                        + " -> "
                        + disruptedFlight.getDestination()
        );

        System.out.println(
                "Flight: "
                        + disruptedFlight.getFlightNumber()
        );

        System.out.println(
                "Departure: "
                        + disruptedFlight.getDepartureTime()
        );

        Disruption disruption =
                new Disruption(
                        disruptedFlight,
                        "Flight cancelled"
                );

        ReplanningService replanningService =
                new ReplanningService(tripPlanner);

        List<Itinerary> alternatives =
                replanningService.replan(
                        request,
                        disruption
                );

        System.out.println();
        System.out.println(
                "Replacement routes found: "
                        + alternatives.size()
        );
        System.out.println();

        if (alternatives.isEmpty()) {

            System.out.println(
                    "PASS: No valid replacement exists after the disruption."
            );

        } else {

            System.out.println(
                    "FAIL: Unexpected replacement route was found."
            );
        }

        NotificationService notificationService = new NotificationService();

        Itinerary replacementItinerary = alternatives.isEmpty() ? null : alternatives.get(0);

        Notification notification = notificationService.createDisruptionNotification(
                disruption,
                replacementItinerary
        );

        System.out.println();

        System.out.println(
                "Notification type: "
                        + notification.getType()
        );

        System.out.println(
                "Notification message: "
                        + notification.getMessage()
        );
    }

    private static void testBookingNotFound() {
        BookingRepository bookingRepository = new BookingRepository();

        System.out.println(
                "=== Test: Booking Not Found ==="
        );

        Optional<Booking> result = bookingRepository.findById(
                "UNKNOWN-ID"
        );

        if (result.isEmpty()) {
            System.out.println(
                    "PASS: Unknown booking ID returned no result."
            );
        } else {
            System.out.println(
                    "FAIL: Unexpected booking was returned."
            );
        }
    }


}
