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
    private boolean inAppartment;
    private TripUserStatus status;

    public TripUser isInAppartment(boolean inAppartment) {
        this.inAppartment = inAppartment;
        return this;
    }
}
