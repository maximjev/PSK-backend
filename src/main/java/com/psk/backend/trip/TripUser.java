package com.psk.backend.trip;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripUser {

    private String id;
    private String name;
    private String surname;
    private String email;
    private boolean inApartment;
    private TripUserStatus status;

    public TripUser isInApartment(boolean inApartment) {
        this.inApartment = inApartment;
        return this;
    }
}
