package com.psk.backend.domain.calendar.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventView {
    private String name;
    private String description;
    private List<EventUserView> users;

    private LocalDateTime start;

    private LocalDateTime end;
}
