package com.psk.backend.service;

import com.psk.backend.domain.calendar.EventRepository;
import com.psk.backend.domain.calendar.value.EventForm;
import com.psk.backend.domain.calendar.value.EventListView;
import com.psk.backend.domain.calendar.value.EventView;
import com.psk.backend.domain.common.EntityId;
import com.psk.backend.domain.trip.TripRepository;
import com.psk.backend.domain.user.AuditUser;
import com.psk.backend.domain.user.UserRepository;
import io.atlassian.fugue.Try;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EventManagementService {

    private final TripRepository tripRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    public EventManagementService(TripRepository tripRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<EventListView> list(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(u -> {
                    var list = eventRepository.list(u.getId());
                    list.addAll(tripRepository.eventListView(u.getId()));
                    return list;
                })
                .getOrElse(Collections::emptyList);
    }

    public List<EventListView> forUser(String id) {
        var list = eventRepository.list(id);
        list.addAll(tripRepository.eventListView(id));
        return list;
    }

    public Try<EntityId> create(EventForm form, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(u -> eventRepository.insert(form, u));
    }

    public Try<EntityId> delete(String id, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(u -> eventRepository.delete(id, AuditUser.of(u)));
    }

    public Try<EntityId> update(String id, EventForm eventForm, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(u -> eventRepository.update(id, eventForm, AuditUser.of(u)));
    }

    public Try<EventView> get(String id, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(u -> eventRepository.get(id, AuditUser.of(u)));
    }
}
