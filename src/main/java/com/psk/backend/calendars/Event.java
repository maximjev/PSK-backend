package com.psk.backend.calendars;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Event {

    @Id
    private String id;

    private String text;

    private LocalDateTime start;

    private LocalDateTime end;
}