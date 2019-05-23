package com.psk.backend.calendars.value;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventListView {
    private String id;
	private String text;
    private LocalDateTime start;
    private LocalDateTime end;
}