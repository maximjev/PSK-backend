package com.psk.backend.domain.trip.value;

import com.psk.backend.domain.common.validation.ValidApartment;
import com.psk.backend.domain.trip.validation.ValidTripApartments;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ValidTripApartments
public class TripCreateForm extends TripForm {

    @NotEmpty
    @ValidApartment
    private String source;

    @NotEmpty
    @ValidApartment
    private String destination;
}
