package com.psk.backend.domain.calendar.value;

import com.psk.backend.domain.validation.ValidUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUserForm {

    @ValidUser
    private String userId;
}