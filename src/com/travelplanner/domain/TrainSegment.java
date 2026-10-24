package com.travelplanner.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TrainSegment extends TravelSegment {

    private String trainNumber;
    private String trainName;

    public TrainSegment(
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            BigDecimal price,
            String trainNumber,
            String trainName) {

        super(
                origin,
                destination,
                departureTime,
                arrivalTime,
                price
        );

        this.trainNumber = trainNumber;
        this.trainName = trainName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }
}