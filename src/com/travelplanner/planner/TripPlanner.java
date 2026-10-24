package com.travelplanner.planner;

import com.travelplanner.domain.*;
import com.travelplanner.provider.MockCabProviderAdapter;
import com.travelplanner.provider.MockFlightProviderAdapter;
import com.travelplanner.provider.MockHotelProviderAdapter;
import com.travelplanner.provider.MockTrainProviderAdapter;
import com.travelplanner.ranking.RankingStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TripPlanner {
    private MockFlightProviderAdapter flightProvider;
    private MockTrainProviderAdapter trainProvider;
    private MockCabProviderAdapter cabProvider;
    private MockHotelProviderAdapter hotelProvider;

    private long transferBufferMinutes;

    private RankingStrategy rankingStrategy;

    private static final int MAX_SEGMENTS = 4;


    public TripPlanner(
            MockFlightProviderAdapter flightProvider,
            MockTrainProviderAdapter trainProvider,
            long transferBufferMinutes
    ) {
        this.flightProvider = flightProvider;
        this.trainProvider = trainProvider;
        this.cabProvider = new MockCabProviderAdapter();
        this.transferBufferMinutes = transferBufferMinutes;
    }

    public TripPlanner(
            MockFlightProviderAdapter flightProvider,
            MockTrainProviderAdapter trainProvider,
            MockCabProviderAdapter cabProvider,
            long transferBufferMinutes
    ) {
        this.flightProvider = flightProvider;
        this.trainProvider = trainProvider;
        this.cabProvider = cabProvider;
        this.transferBufferMinutes = transferBufferMinutes;
    }

    public TripPlanner(
            MockFlightProviderAdapter flightProvider,
            MockTrainProviderAdapter trainProvider,
            MockCabProviderAdapter cabProvider,
            MockHotelProviderAdapter hotelProvider,
            long transferBufferMinutes) {

        this.flightProvider = flightProvider;
        this.trainProvider = trainProvider;
        this.cabProvider = cabProvider;
        this.hotelProvider = hotelProvider;
        this.transferBufferMinutes = transferBufferMinutes;
    }

    public List<Itinerary> search(TripRequest request) {
        List<Itinerary> itineraries = new ArrayList<>();
        List<TravelSegment> allSegments = new ArrayList<>();

        allSegments.addAll(flightProvider.search(request));

        allSegments.addAll(trainProvider.search(request));

        allSegments.addAll(cabProvider.search(request));

        List<HotelReservation> hotels = hotelProvider.search(request);


        List<TravelSegment> currentPath = new ArrayList<>();

        Set<String> visitedLocations  = new HashSet<>();

        visitedLocations.add(request.getOrigin());

        LocalDateTime earliestDeparture = request.getDepartureDate().atStartOfDay();

        findRoutes(
                request.getOrigin(),
                request.getDestination(),
                earliestDeparture,
                allSegments,
                currentPath,
                visitedLocations,
                itineraries
        );

        return itineraries;

    }

    private void findRoutes(
            String currentLocation,
            String finalDestination,
            LocalDateTime earliestDeparture,
            List<TravelSegment> allSegments,
            List<TravelSegment> currentPath,
            Set<String> visitedLocations,
            List<Itinerary> itineraries
    ) {
        if (currentPath.size() > MAX_SEGMENTS) {
            return;
        }

        for (TravelSegment segment : allSegments) {
            boolean startsHere = segment.getOrigin()
                    .equalsIgnoreCase(currentLocation);

            if (!startsHere) {
                continue;
            }

            boolean leavesLateEnough = !segment.getDepartureTime()
                    .isBefore(earliestDeparture);

            if (!leavesLateEnough) {
                continue;
            }

            String nextLocation = segment.getDestination();

            if (visitedLocations.contains(
                    nextLocation
            )) {
                continue;
            }

            currentPath.add(segment);

            if (nextLocation.equalsIgnoreCase(
                    finalDestination
            )) {
                Itinerary itinerary = buildItinerary(currentPath);

                itineraries.add(itinerary);
            }
            else {
                visitedLocations.add(nextLocation);

                LocalDateTime nextEarliestDeparture = segment.getArrivalTime()
                        .plusMinutes(transferBufferMinutes);

                findRoutes(
                        nextLocation,
                        finalDestination,
                        nextEarliestDeparture,
                        allSegments,
                        currentPath,
                        visitedLocations,
                        itineraries
                );

                visitedLocations.remove(nextLocation);
            }

            currentPath.remove(currentPath.size() - 1);
        }
    }

    private Itinerary buildItinerary(List<TravelSegment> path) {
        Itinerary itinerary = new Itinerary();

        for (TravelSegment segment : path) {
            itinerary.addSegment(segment);
        }

        return itinerary;
    }

    public void setRankingStrategy(RankingStrategy rankingStrategy) {
        this.rankingStrategy = rankingStrategy;
    }

    public List<Itinerary> rankItineraries(
            List<Itinerary> itineraries
    ) {
        if (rankingStrategy == null) {
            throw new IllegalStateException(
                    "Ranking strategy has not been set"
            );
        }

        return rankingStrategy.rank(itineraries);
    }

    private void attachHotelIfValid(
            Itinerary itinerary,
            List<HotelReservation> hotels
    ) {
        if (itinerary.getSegments().isEmpty()) {
            return;
        }

        TravelSegment lastSegment =
                itinerary.getSegments()
                        .get(
                                itinerary.getSegments().size() - 1
                        );

        LocalDate arrivalDate = lastSegment.getArrivalTime().toLocalDate();

        for (HotelReservation hotel : hotels) {
            boolean correctLocation =
                    hotel.getLocation()
                            .equalsIgnoreCase(
                                    lastSegment.getDestination()
                            );

            boolean validCheckIn = hotel.getCheckInDate().isEqual(arrivalDate);

            boolean validStay = hotel.getCheckOutDate()
                    .isAfter(
                        hotel.getCheckInDate()
                    );

            if (correctLocation
                && validCheckIn
                && validStay) {
                itinerary.setHotelReservation(hotel);
                break;
            }
        }
    }



}
