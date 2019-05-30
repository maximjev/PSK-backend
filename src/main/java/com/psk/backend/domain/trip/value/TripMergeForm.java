package com.psk.backend.domain.trip.value;

import com.psk.backend.domain.common.validation.ValidTrip;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripMergeForm {

    @ValidTrip
    private String tripOne;

    @ValidTrip
    private String tripTwo;
}

