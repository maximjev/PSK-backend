package com.psk.backend.calendar.value;

import com.psk.backend.calendar.EventUserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUserView {
    private String id;
    private String name;
    private String surname;
    private String email;
    private EventUserStatus status;
}
