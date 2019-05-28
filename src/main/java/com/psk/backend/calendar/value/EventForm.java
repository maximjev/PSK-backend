package com.psk.backend.calendar.value;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventForm {

    @NotEmpty
    private String name;

	@NotEmpty
    private String description;

	@Valid
    private List<EventUserForm> users;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}