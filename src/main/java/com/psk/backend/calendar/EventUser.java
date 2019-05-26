package com.psk.backend.calendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUser {

    private String id;
    private String name;
    private String surname;
    private String email;
    private boolean atEvent;
    private EventUserStatus status;

    public EventUser isAtEvent(boolean atEvent) {
        this.atEvent = atEvent;
        return this;
    }
}