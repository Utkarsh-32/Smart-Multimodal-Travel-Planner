package com.travelplanner.provider;

import com.travelplanner.domain.CabSegment;
import com.travelplanner.domain.ProviderAdapter;
import com.travelplanner.domain.TripRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockCabProviderAdapter implements ProviderAdapter<CabSegment> {
    private boolean reservationShouldSucceed;

    public MockCabProviderAdapter() {
        this.reservationShouldSucceed = true;
    }

    public MockCabProviderAdapter(
            boolean reservationShouldSucceed
    ) {
        this.reservationShouldSucceed = reservationShouldSucceed;
    }

    @Override
    public List<CabSegment> search(TripRequest request) {
        List<CabSegment> cabs = new ArrayList<>();

        CabSegment cab1 = new CabSegment(
                "Jaipur",
                "Ajmer",
                LocalDateTime.of(2026,11,10,11,30),
                LocalDateTime.of(2026,11,10,13,30),
                new BigDecimal("1200"),
                "Ola",
                "Sedan",
                "RJ14AB1234"
        );

        cabs.add(cab1);

        return cabs;
    }

    @Override
    public boolean reserve(CabSegment option) {
        return reservationShouldSucceed;
    }

    @Override
    public boolean cancel(CabSegment option) {
        return true;
    }
}
