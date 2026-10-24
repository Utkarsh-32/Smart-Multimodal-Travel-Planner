package com.travelplanner.demo;

import com.travelplanner.booking.BookingFacade;
import com.travelplanner.domain.*;
import com.travelplanner.provider.MockCabProviderAdapter;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingDemo {
    public static void main(String[] args) {
        Itinerary itinerary = new Itinerary();

        TrainSegment train = new TrainSegment(
                "Delhi",
                "Jaipur",
                LocalDateTime.of(2026, 11, 10, 6, 0),
                LocalDateTime.of(2026, 11, 10, 10, 0),
                new BigDecimal("800"),
                "12958",
                "Delhi Jaipur Express"
        );

        CabSegment cab = new CabSegment(
                "Jaipur",
                "Ajmer",
                LocalDateTime.of(2026, 11, 10, 11, 30),
                LocalDateTime.of(2026, 11, 10, 13, 30),
                new BigDecimal("1200"),
                "Ola",
                "Sedan",
                "RJ14AB1234"
        );

        FlightSegment flight = new FlightSegment(
                "Ajmer",
                "Mumbai",
                LocalDateTime.of(2026, 11, 10, 15, 0),
                LocalDateTime.of(2026, 11, 10, 16, 30),
                new BigDecimal("3200"),
                "AI777",
                "Air India"
        );

        itinerary.addSegment(train);
        itinerary.addSegment(cab);
        itinerary.addSegment(flight);

        testConfirmed(itinerary);
        System.out.println();
        testPartiallyConfirmed(itinerary);
        System.out.println();
        testFailed(itinerary);
    }

    private static void testConfirmed(Itinerary itinerary) {
        BookingFacade facade = new BookingFacade(
                new MockFlightProviderAdapter(true),
                new MockTrainProviderAdapter(true),
                new MockCabProviderAdapter(true)
        );

        Booking booking = facade.book("B001", itinerary);

        System.out.println("Test 1 status: " + booking.getStatus());

        boolean cancelled = facade.cancel(booking);

        System.out.println(
                "Cancellation successful: "
                        + cancelled
        );

        System.out.println(
                "Status after cancellation: "
                        + booking.getStatus()
        );
    }


    private static void testPartiallyConfirmed(
            Itinerary itinerary) {

        BookingFacade facade =
                new BookingFacade(
                        new MockFlightProviderAdapter(true),
                        new MockTrainProviderAdapter(false),
                        new MockCabProviderAdapter(true)
                );

        Booking booking =
                facade.book(
                        "B002",
                        itinerary
                );

        System.out.println(
                "Test 2 status: "
                        + booking.getStatus()
        );
    }

    private static void testFailed(Itinerary itinerary) {
        BookingFacade facade =
                new BookingFacade(
                        new MockFlightProviderAdapter(false),
                        new MockTrainProviderAdapter(false),
                        new MockCabProviderAdapter(false)
                );

        Booking booking =
                facade.book(
                        "B003",
                        itinerary
                );

        System.out.println(
                "Test 3 status: "
                        + booking.getStatus()
        );
    }
}
