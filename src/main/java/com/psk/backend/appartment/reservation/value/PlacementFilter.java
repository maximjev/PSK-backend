package com.psk.backend.appartment.reservation.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlacementFilter {
    private LocalDateTime from;
    private LocalDateTime till;
}
