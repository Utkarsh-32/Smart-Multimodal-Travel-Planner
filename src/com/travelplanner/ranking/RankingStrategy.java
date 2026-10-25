package com.travelplanner.ranking;

import com.travelplanner.domain.Itinerary;

import java.util.List;

public interface RankingStrategy {
    List<Itinerary> rank(List<Itinerary> itineraries);

}
