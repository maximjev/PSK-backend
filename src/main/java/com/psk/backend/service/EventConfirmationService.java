package com.psk.backend.service;

import com.psk.backend.repository.EventRepository;
import com.psk.backend.domain.calendar.EventUserStatus;
import com.psk.backend.domain.common.EntityId;
import com.psk.backend.repository.UserRepository;
import io.atlassian.fugue.Try;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
public class EventConfirmationService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public EventConfirmationService(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public Try<EntityId> confirm(String id, Authentication authentication) {
        return updateStatus(id, authentication, EventUserStatus.CONFIRMED);
    }

    public Try<EntityId> decline(String id, Authentication authentication) {
        return updateStatus(id, authentication, EventUserStatus.DECLINED);
    }

    private Try<EntityId> updateStatus(String id, Authentication authentication, EventUserStatus status) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(u -> eventRepository.updateStatus(id, u.getId(), status));
    }
}
