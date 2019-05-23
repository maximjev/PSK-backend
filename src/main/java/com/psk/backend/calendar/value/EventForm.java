package com.psk.backend.calendars.value;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventForm {

	@NotEmpty
    private String text;

    private List<EventUser> users;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}