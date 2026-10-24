package com.travelplanner.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HotelReservation {
    private String hotelName;
    private String location;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BigDecimal price;

    public HotelReservation(
            String hotelName,
            String location,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            BigDecimal price
    ) {
        this.hotelName = hotelName;
        this.location = location;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.price = price;
    }
}
