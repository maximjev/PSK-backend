package com.psk.backend.trip.value;

import com.psk.backend.common.validation.ValidUser;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TripUserForm {

    @ValidUser
    private String userId;

    @NotNull
    private boolean inApartment;

    private String residenceAddress;

    private String carRent;

    private String flightTicket;
}
