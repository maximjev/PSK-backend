package com.psk.backend.trip;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TripUser {

    private String id;
    private String name;
    private String surname;
    private String email;
    private boolean inApartment;
    private TripUserStatus status;

    private String residenceAddress;
    private String carRent;
    private String flightTicket;

    public TripUser isInApartment(boolean inApartment) {
        this.inApartment = inApartment;
        return this;
    }
}
