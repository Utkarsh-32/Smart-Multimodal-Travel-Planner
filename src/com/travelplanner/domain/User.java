package com.travelplanner.domain;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;

    private List<TripRequest> tripRequests;

    public User(
            String userId,
            String name
    ) {
        this.userId = userId;
        this.name = name;
        this.tripRequests = new ArrayList<>();
    }
}
