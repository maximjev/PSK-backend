package com.psk.backend.trip.value;

import com.psk.backend.common.validation.ValidApartment;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class TripCreateForm extends TripForm {

    @NotEmpty
    @ValidApartment
    private String source;

    @NotEmpty
    @ValidApartment
    private String destination;
}
