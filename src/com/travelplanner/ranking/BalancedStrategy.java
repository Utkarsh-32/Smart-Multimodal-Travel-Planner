package com.travelplanner.ranking;

import com.travelplanner.domain.Itinerary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BalancedStrategy implements RankingStrategy {
    @Override
    public List<Itinerary> rank(List<Itinerary> itineraries) {
        List<Itinerary> rankedItineraries = new ArrayList<>(itineraries);

        if (rankedItineraries.isEmpty()) {
            return rankedItineraries;
        }

        double minCost = rankedItineraries.stream()
                .mapToDouble(
                        itinerary -> itinerary.getTotalCost().doubleValue()
                )
                .min()
                .orElse(0);

        double maxCost = rankedItineraries.stream()
                .mapToDouble(
                        itinerary -> itinerary.getTotalCost().doubleValue()
                )
                .max()
                .orElse(0);

        long minDuration = rankedItineraries.stream()
                .mapToLong(
                        Itinerary::getTotalDurationMinutes
                )
                .min()
                .orElse(0);

        long maxDuration = rankedItineraries.stream()
                .mapToLong(
                        Itinerary::getTotalDurationMinutes
                )
                .max()
                .orElse(0);

        rankedItineraries.sort(
                Comparator.comparingDouble(
                        itinerary->calculateScore(
                                itinerary,
                                minCost,
                                maxCost,
                                minDuration,
                                maxDuration
                        )
                )
        );

        return rankedItineraries;
    }

    private double calculateScore(
            Itinerary itinerary,
            double minCost,
            double maxCost,
            long minDuration,
            long maxDuration
    ) {
        double cost = itinerary
                .getTotalCost()
                .doubleValue();

        long duration = itinerary.getTotalDurationMinutes();

        double normalizedCost = 0;

        if (maxCost != minCost) {
            normalizedCost =
                    (cost - minCost) / (maxCost - minCost);
        }

        double normalizedDuration = 0;

        if (minDuration != maxDuration) {
            normalizedDuration =
                    (double)  (duration - minDuration) / (maxDuration - minDuration);
        }

        return 0.5 * normalizedCost + 0.5 * normalizedDuration;
    }
}
