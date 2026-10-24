package com.travelplanner.domain;

public class Booking {

    private String bookingId;

    private Itinerary itinerary;

    private BookingStatus status;

    public Booking(
            String bookingId,
            Itinerary itinerary
    ) {
        this.bookingId = bookingId;
        this.itinerary = itinerary;
        this.status = BookingStatus.CREATED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void updateStatus(BookingStatus status) {
        this.status = status;
    }
}
