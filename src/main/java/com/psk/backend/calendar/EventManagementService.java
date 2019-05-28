package com.psk.backend.calendar;

import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.calendar.value.EventView;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.TripRepository;
import com.psk.backend.user.AuditUser;
import com.psk.backend.user.UserRepository;
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
