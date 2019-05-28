package com.psk.backend.calendar;

import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventControllerService {

    private final EventManagementService service;

    public EventControllerService(EventManagementService service) {
        this.service = service;
    }

    public List<EventListView> list(Authentication authentication) {
        return service.list(authentication);
    }

    public Try<EntityId> create(EventForm form, Authentication authentication) {
        return service.create(form, authentication);
    }

    public Try<EntityId> update(String id, EventForm form, Authentication authentication) {
        return service.update(id, form, authentication);
    }

    public Try<EntityId> delete(String id, Authentication authentication) {
        return service.delete(id, authentication);
    }
}