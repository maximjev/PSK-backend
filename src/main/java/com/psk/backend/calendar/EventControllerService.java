package com.psk.backend.calendar;

import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.calendar.value.EventView;

@Service
public class EventControllerService {

    private final EventRepository eventRepository;

    public EventControllerService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Page<EventListView> list(Pageable page) {
        return eventRepository.list(page);
    }

    public Try<EntityId> create(EventForm form) {
        return eventRepository.insert(form);
    }

    public Try<EntityId> update(String id, EventForm form) {
        return eventRepository.update(id, form);
    }

    public Try<EventView> get(String id) {
        return eventRepository.get(id);
    }

    public Try<EntityId> delete(String id) {
        return eventRepository.delete(id);
    }
}