package com.psk.backend.apartment.reservation.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlacementFilter {
    private LocalDateTime from;
    private LocalDateTime till;

    public PlacementFilter(LocalDateTime from, LocalDateTime till) {
        this.from = from;
        this.till = till;
    }
}
