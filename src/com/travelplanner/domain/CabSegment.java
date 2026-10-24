package com.travelplanner.domain;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CabSegment extends TravelSegment {
    private String provider;
    private String vehicleType;
    private String vehicleNumber;

    public CabSegment (
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            BigDecimal price,
            String provider,
            String vehicleType,
            String vehicleNumber
    ) {
        super (
                origin,
                destination,
                departureTime,
                arrivalTime,
                price
        );

        this.provider = provider;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
    }

    public String getProvider() {
        return provider;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}
