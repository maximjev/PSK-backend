package com.psk.backend.mapper;

import com.psk.backend.domain.trip.Trip;
import com.psk.backend.domain.trip.value.TripUserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class TripMapperDecorator extends TripMapper {

    @Autowired
    @Qualifier("delegate")
    private TripMapper delegate;

    @Override
    public TripUserView tripUserView(Trip trip, String userId) {
        var userView = delegate.tripUserView(trip, userId);
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
