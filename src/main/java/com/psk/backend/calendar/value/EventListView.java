package com.psk.backend.calendar.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventListView {
    private String id;
	private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean owner;
    private boolean trip;
}