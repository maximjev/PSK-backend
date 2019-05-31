package com.psk.backend.domain.trip.value;

import com.psk.backend.domain.trip.TripStatus;
import com.psk.backend.domain.trip.TripUserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripUserView {
    private String tripId;
    private String name;
    private TripStatus status;
    private LocalDateTime departure;
    private String sourceAddress;
    private String residenceAddress;
    private String carRent;
    private String flightTicket;
    private TripUserStatus userStatus;
}
