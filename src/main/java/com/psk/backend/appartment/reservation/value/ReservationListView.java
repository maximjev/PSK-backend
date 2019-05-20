package com.psk.backend.appartment.reservation.value;

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
    private LocalDateTime to;
    private LocalDateTime createdAt;
    private String createdBy;
}
