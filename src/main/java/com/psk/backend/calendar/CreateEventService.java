package com.psk.backend.calendar;

import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;


@Service
public class CreateEventService {

    private final EventRepository eventRepository;

    public CreateEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Try<EntityId> create(EventForm form) {
        return eventRepository.insert(form);
    }
}