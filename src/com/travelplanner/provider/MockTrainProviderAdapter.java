package com.travelplanner.provider;

import com.travelplanner.domain.ProviderAdapter;
import com.travelplanner.domain.TrainSegment;
import com.travelplanner.domain.TripRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockTrainProviderAdapter implements ProviderAdapter<TrainSegment> {
    private boolean reservationShouldSucceed;

    public MockTrainProviderAdapter() {
        this.reservationShouldSucceed = true;
    }

    public MockTrainProviderAdapter(
            boolean reservationShouldSucceed
    ) {
        this.reservationShouldSucceed = reservationShouldSucceed;
    }
    @Override
    public List<TrainSegment> search(TripRequest request) {
        List<TrainSegment> trains = new ArrayList<>();

        TrainSegment train1 = new TrainSegment(
                "Delhi",
                "Jaipur",
                LocalDateTime.of(2026,11,10,6,0),
                LocalDateTime.of(2026,11,10,10,0),
                new BigDecimal("800"),
                "12952",
                "Delhi Jaipur Express"
        );

        TrainSegment train2 = new TrainSegment(
                "Delhi",
                "Mumbai",
                LocalDateTime.of(2026, 11, 10, 16, 0),
                LocalDateTime.of(2026, 11, 11, 8, 30),
                new BigDecimal("1500"),
                "12954",
                "August Kranti Rajdhani"
        );

        trains.add(train1);
        trains.add(train2);

        return trains;
    }

    @Override
    public boolean reserve(TrainSegment option) {
        return reservationShouldSucceed;
    }

    @Override
    public boolean cancel(TrainSegment option) {
        return true;
    }

}
