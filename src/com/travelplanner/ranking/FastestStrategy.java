package com.travelplanner.ranking;

import com.travelplanner.domain.Itinerary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FastestStrategy implements RankingStrategy {
    @Override
    public List<Itinerary> rank(List<Itinerary> itineraries) {
        List<Itinerary> rankedItineraries = new ArrayList<>(itineraries);

        rankedItineraries.sort(
                Comparator.comparingLong(
                        Itinerary::getTotalDurationMinutes
                )
        );

        return rankedItineraries;

    }
}
