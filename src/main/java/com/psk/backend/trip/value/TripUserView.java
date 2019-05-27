package com.psk.backend.trip.value;

import com.psk.backend.common.address.AddressView;
import com.psk.backend.trip.TripStatus;
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
    private AddressView sourceAddress;
    private AddressView residenceAddress;
    private String carRent;
    private String flightTicket;
}
