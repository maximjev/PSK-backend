package com.psk.backend.calendar;

import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.EventForm;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;

import static com.psk.backend.common.Error.UNEXPECTED_ERROR;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;

@Service
public class CreateEventService {

    private final EventRepository EventRepository;

    public CreateEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Try<EntityId> create(EventForm form) {
        eventRepository.insert(form);
    }

}