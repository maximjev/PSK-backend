package com.psk.backend.calendar.value;

import com.psk.backend.calendar.EventUser;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventForm {

	@NotEmpty
    private String description;

    private List<EventUser> users;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}