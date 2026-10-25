package com.travelplanner.booking;

import com.travelplanner.domain.*;

public class BookingFacade {
    private ProviderAdapter<FlightSegment> flightProvider;
    private ProviderAdapter<TrainSegment> trainProvider;
    private ProviderAdapter<CabSegment> cabProvider;
    private ProviderAdapter<HotelReservation> hotelProvider;

    public BookingFacade (
            ProviderAdapter<FlightSegment> flightProvider,
            ProviderAdapter<TrainSegment> trainProvider,
            ProviderAdapter<CabSegment> cabProvider,
            ProviderAdapter<HotelReservation> hotelProvider
    ) {
        this.flightProvider = flightProvider;
        this.trainProvider = trainProvider;
        this.cabProvider = cabProvider;
        this.hotelProvider = hotelProvider;
    }

    public Booking book(
            String bookingId,
            Itinerary itinerary
    ) {
        Booking booking = new Booking(bookingId, itinerary);

        int successfulReservations = 0;

        int totalReservations = itinerary.getSegments().size();

        if (itinerary.getHotelReservation() != null) {
            totalReservations++;
        }

        for (TravelSegment segment : itinerary.getSegments()) {
            boolean success = reserveSegment(segment);
            if (success) {
                successfulReservations++;
            }
        }

        if (itinerary.getHotelReservation() != null) {
            boolean hotelSuccess = hotelProvider.reserve(itinerary.getHotelReservation());
            if (hotelSuccess) {
                successfulReservations++;
            }
        }

        if (totalReservations == 0 || successfulReservations == 0) {
            booking.updateStatus(BookingStatus.FAILED);
        } else if (totalReservations == successfulReservations) {
            booking.updateStatus(BookingStatus.CONFIRMED);
        } else {
            booking.updateStatus(BookingStatus.PARTIALLY_CONFIRMED);
        }

        return booking;
    }

    private boolean reserveSegment(TravelSegment segment) {
        if (segment instanceof FlightSegment flight) {
            return flightProvider.reserve(flight);
        }

        else if (segment instanceof TrainSegment train) {
            return trainProvider.reserve(train);
        }

        else if (segment instanceof CabSegment cab) {
            return cabProvider.reserve(cab);
        }

        return false;
    }

    public boolean cancel(Booking booking) {
        boolean allCancelled = true;

        for (TravelSegment segment : booking.getItinerary().getSegments()) {
            boolean cancelled = cancelSegment(segment);

            if (!cancelled) {
                allCancelled = false;
            }
        }

        if (booking.getItinerary()
                .getHotelReservation() != null) {

            boolean hotelCancelled =
                    hotelProvider.cancel(
                            booking.getItinerary()
                                    .getHotelReservation()
                    );

            if (!hotelCancelled) {
                allCancelled = false;
            }
        }

        if (allCancelled) {
            booking.updateStatus(BookingStatus.CANCELLED);
        }

        return allCancelled;
    }

    private boolean cancelSegment(TravelSegment segment) {
        if (segment instanceof FlightSegment flight) {
            return flightProvider.cancel(flight);
        }
        else if (segment instanceof TrainSegment train) {
            return trainProvider.cancel(train);
        }
        else if (segment instanceof CabSegment cab) {
            return cabProvider.cancel(cab);
        }
        return false;
    }

}
