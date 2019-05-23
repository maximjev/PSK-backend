package com.psk.backend.calendars.value;

import com.psk.backend.trip.validation.ValidUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUserForm {

    @ValidUser
    private String userId;

    private boolean atEvent; //?
}