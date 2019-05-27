package com.psk.backend.calendar;

import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.calendar.value.EventView;
import com.psk.backend.common.EntityId;
import com.psk.backend.user.UserRepository;
import io.atlassian.fugue.Try;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EventControllerService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    public EventControllerService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<EventListView> list(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(u -> eventRepository.list(u.getId()))
                .getOrElse(Collections::emptyList);
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