package com.psk.backend.domain.calendar.value;

import com.psk.backend.domain.calendar.validation.ValidEventForm;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ValidEventForm
public class EventForm {

    @NotEmpty
    private String name;

	@NotEmpty
    private String description;

    private List<String> users;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}