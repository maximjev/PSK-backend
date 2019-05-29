package com.psk.backend.calendar;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.UserRepository;
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
