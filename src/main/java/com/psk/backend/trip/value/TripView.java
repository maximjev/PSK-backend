package com.psk.backend.trip.value;

import com.psk.backend.common.address.AddressForm;
import com.psk.backend.trip.TripStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TripView {
    private String id;
    private String name;
    private AddressForm source;
    private AddressForm destination;
    private boolean noReservation;
    private TripStatus status;
    private LocalDateTime departure;
    private LocalDateTime reservationBegin;
    private LocalDateTime reservationEnd;

    private ExpensesForm hotel;
    private ExpensesForm flight;

    private List<TripUserForm> users;
}
