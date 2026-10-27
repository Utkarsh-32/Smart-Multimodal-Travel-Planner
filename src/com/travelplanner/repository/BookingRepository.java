package com.travelplanner.repository;

import com.travelplanner.domain.Booking;

import java.util.*;

public class BookingRepository {
    private final Map<String, Booking> bookings =  new HashMap<>();

    public void save(Booking booking) {
        bookings.put(
                booking.getBookingId(),
                booking
        );
    }

    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(
                bookings.get(bookingId)
        );
    }

    public List<Booking> findAll() {
        return new ArrayList<>(
                bookings.values()
        );
    }


}
