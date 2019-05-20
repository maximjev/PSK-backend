package com.psk.backend.trip.value;

import com.psk.backend.trip.validation.ValidAppartment;
import com.psk.backend.trip.validation.ValidUsers;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TripForm {

    @ValidAppartment
    private String source;

    @ValidAppartment
    private String destination;

    @ValidUsers
    private List<String> users;

    @NotNull
    private LocalDateTime departion;

    @NotNull
    private LocalDateTime reservationBegin;

    @NotNull
    private LocalDateTime reservationEnd;
}
