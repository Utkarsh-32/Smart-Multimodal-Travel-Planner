package com.travelplanner.provider;

import com.travelplanner.domain.HotelReservation;
import com.travelplanner.domain.ProviderAdapter;
import com.travelplanner.domain.TripRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockHotelProviderAdapter implements ProviderAdapter<HotelReservation> {
    private boolean reservationShouldSucceed;

    public MockHotelProviderAdapter()  {
        this.reservationShouldSucceed = true;
    }

    public MockHotelProviderAdapter(
            boolean reservationShouldSucceed
    ) {
        this.reservationShouldSucceed = reservationShouldSucceed;
    }

    @Override
    public List<HotelReservation> search(TripRequest request) {
        List<HotelReservation> hotels = new ArrayList<>();

        HotelReservation hotel1 = new HotelReservation(
                "Mumbai Central Hotel",
                "Mumbai",
                LocalDate.of(2026,11,10),
                LocalDate.of(2026,11,11),
                new BigDecimal("2500")
        );

        HotelReservation hotel2 =
                new HotelReservation(
                        "Sea View Residency",
                        "Mumbai",
                        LocalDate.of(2026, 11, 10),
                        LocalDate.of(2026, 11, 11),
                        new BigDecimal("3200")
                );

        hotels.add(hotel1);
        hotels.add(hotel2);

        return hotels;
    }

    @Override
    public boolean reserve(HotelReservation option) {
        return reservationShouldSucceed;
    }

    @Override
    public boolean cancel(HotelReservation option) {
        return true;
    }
}
