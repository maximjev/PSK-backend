package com.psk.backend.appartment.reservation.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationForm {

    private Long places;

    private LocalDateTime from;

    private LocalDateTime till;

    private String appartmentId;

    private String tripId;

    public ReservationForm withTrip(String tripId) {
        this.tripId = tripId;
        return this;
    }
}
