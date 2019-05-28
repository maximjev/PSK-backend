package com.psk.backend.calendar.value;

import com.psk.backend.calendar.EventUserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventListView {
    private String id;
	private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private EventUserStatus userStatus;
    private boolean owner;
    private boolean trip;
}