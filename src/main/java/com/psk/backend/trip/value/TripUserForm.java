package com.psk.backend.trip.value;

import com.psk.backend.trip.validation.ValidUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripUserForm {

    @ValidUser
    private String userId;

    private boolean inAppartment;
}
