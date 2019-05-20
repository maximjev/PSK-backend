package com.psk.backend.trip.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripView {
    private String source;
    private String destination;
    private LocalDateTime departion;
    private LocalDateTime reservationBegin;
    private LocalDateTime reservationEnd;
}
