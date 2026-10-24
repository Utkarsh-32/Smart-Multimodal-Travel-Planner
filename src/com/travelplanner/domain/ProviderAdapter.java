package com.travelplanner.domain;

import java.util.List;

public interface ProviderAdapter<T> {
    List<T> search(TripRequest request);

    boolean reserve(T option);

    boolean cancel(T option);
}
