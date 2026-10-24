package com.travelplanner.disruption;

import com.travelplanner.domain.Itinerary;
import com.travelplanner.domain.TravelSegment;
import com.travelplanner.domain.TripRequest;
import com.travelplanner.planner.TripPlanner;

import java.util.ArrayList;
import java.util.List;

public class ReplanningService {
    private TripPlanner tripPlanner;

    public ReplanningService(
            TripPlanner tripPlanner
    ) {
        this.tripPlanner = tripPlanner;
    }

    public List<Itinerary> replan(
            TripRequest originalRequest,
            Disruption disruption
    ) {
        TravelSegment affectedSegment = disruption.getAffectedSegment();

        TripRequest newRequest = new TripRequest(
                affectedSegment.getOrigin(),
                originalRequest.getDestination(),
                affectedSegment.getDepartureTime().toLocalDate(),
                originalRequest.getReturnDate(),
                originalRequest.getBudget(),
                originalRequest.getPreferences()
        );

        List<Itinerary> candidates = tripPlanner.search(newRequest);
        List<Itinerary> validAlternatives = new ArrayList<>();

        for (Itinerary itinerary : candidates) {
            if (itinerary.getSegments().isEmpty()) {
                continue;
            }

            boolean containsDisruptedSegment = false;

            for (TravelSegment segment : itinerary.getSegments()) {
                if (sameSegment(
                        segment,
                        affectedSegment
                )) {
                    containsDisruptedSegment = true;
                    break;
                }
            }

            if (containsDisruptedSegment) {
                continue;
            }


            TravelSegment firstSegment = itinerary.getSegments().get(0);

            boolean leavesAfterDisruption =
                    !firstSegment
                            .getDepartureTime()
                            .isBefore(
                                    affectedSegment.getDepartureTime()
                            );

            if (leavesAfterDisruption) {
                validAlternatives.add(itinerary);
            }
        }

        return validAlternatives;
    }

    private boolean sameSegment(
            TravelSegment first,
            TravelSegment second
    ) {
        return first.getClass()
                .equals(second.getClass())

                && first.getOrigin()
                .equalsIgnoreCase(second.getOrigin())

                && first.getDestination()
                .equalsIgnoreCase(second.getDestination())

                && first.getDepartureTime()
                .equals(second.getDepartureTime())

                && first.getArrivalTime()
                .equals(second.getArrivalTime());
    }
}
