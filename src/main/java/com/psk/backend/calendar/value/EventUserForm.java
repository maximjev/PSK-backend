package com.psk.backend.calendar.value;

import com.psk.backend.common.validation.ValidUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUserForm {

    @ValidUser
    private String userId;
}