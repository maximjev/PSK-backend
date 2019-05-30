package com.psk.backend.domain.trip;

import com.psk.backend.domain.trip.value.TripUserView;

public abstract class TripMapperDecorator extends TripMapper {

    @Override
    public TripUserView tripUserView(Trip trip, String userId) {
        var userView = tripUserView(trip, userId);
        var user = trip.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst();
        return user.map(u -> {
            userView.setCarRent(u.getCarRent());
            userView.setFlightTicket(u.getFlightTicket());
            userView.setResidenceAddress(u.getResidenceAddress());
            userView.setUserStatus(u.getStatus());
            return userView;
        }).orElse(userView);
    }
}
