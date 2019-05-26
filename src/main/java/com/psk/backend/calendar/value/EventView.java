package com.psk.backend.calendar.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventView {
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
}