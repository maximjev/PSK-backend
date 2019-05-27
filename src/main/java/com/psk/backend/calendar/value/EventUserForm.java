package com.psk.backend.calendar.value;

import com.psk.backend.common.validation.ValidUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUserForm {

    @ValidUser
    private String userId;

    private boolean atEvent;

    public EventUserForm(String userId, boolean atEvent){
        this.userId=userId;
        this.atEvent=atEvent;
    }
}