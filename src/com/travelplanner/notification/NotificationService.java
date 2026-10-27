package com.travelplanner.notification;

import com.travelplanner.disruption.Disruption;
import com.travelplanner.domain.FlightSegment;
import com.travelplanner.domain.Itinerary;
import com.travelplanner.domain.Notification;
import com.travelplanner.domain.TravelSegment;

public class NotificationService {
    public Notification createDisruptionNotification(
            Disruption disruption,
            Itinerary replacementItinerary
    ) {
        TravelSegment affectedSegment = disruption.getAffectedSegment();
        StringBuilder message = new StringBuilder();

        message.append("Travel disruption: ");

        if (affectedSegment instanceof FlightSegment flight) {
            message.append(
                    "Flight: " + flight.getFlightNumber() + " was disrupted. "
            );
        } else {
            message.append(
                    affectedSegment.getOrigin() + " -> "
                    + affectedSegment.getDestination()
                    + " was disrupted. "
            );
        }

        message.append(
                "Reason: "
                + disruption.getReason()
                + ". "
        );

        if (replacementItinerary != null
            && !replacementItinerary
                .getSegments()
                .isEmpty()) {
            TravelSegment replacement = replacementItinerary.getSegments().get(0);
            message.append("Alternative found: ");

            if (replacement instanceof FlightSegment flight) {
                message.append(
                        "Flight "
                                + flight.getFlightNumber()
                                + " departing at "
                                + flight.getDepartureTime()
                );
            } else {
                message.append(
                        replacement.getOrigin()
                                + " -> "
                                + replacement.getDestination()
                                + " departing at "
                                + replacement.getDepartureTime()
                );
            }
        } else {
            message.append("No replacement itinerary was found.");
        }

        return new Notification(
                "DISRUPTION",
                message.toString()
        );

    }
}
