package com.travelplanner.disruption;

import com.travelplanner.domain.TravelSegment;

public class Disruption {
    private TravelSegment affectedSegment;
    private String reason;

    public Disruption(
            TravelSegment affectedSegment,
            String reason
    ) {
        this.affectedSegment = affectedSegment;
        this.reason = reason;
    }

    public TravelSegment getAffectedSegment() {
        return affectedSegment;
    }

    public String getReason() {
        return reason;
    }
}
