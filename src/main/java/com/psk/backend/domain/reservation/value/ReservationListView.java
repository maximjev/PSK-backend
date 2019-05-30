package com.psk.backend.domain.reservation.value;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ReservationListView {

    private String id;
    private Long places;
    private LocalDateTime from;
    private LocalDateTime till;
    private LocalDateTime createdAt;
}
