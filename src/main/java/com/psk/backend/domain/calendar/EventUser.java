package com.psk.backend.domain.calendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUser {

    private String id;
    private String name;
    private String surname;
    private String email;
    private EventUserStatus status;
}