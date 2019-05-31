package com.psk.backend.facade;

import com.psk.backend.domain.calendar.value.EventForm;
import com.psk.backend.domain.calendar.value.EventListView;
import com.psk.backend.domain.calendar.value.EventView;
import com.psk.backend.domain.common.EntityId;
import com.psk.backend.service.EventConfirmationService;
import com.psk.backend.service.EventManagementService;
import io.atlassian.fugue.Try;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventControllerService {

    private final EventManagementService service;
    private final EventConfirmationService confirmationService;

    public EventControllerService(EventManagementService service, EventConfirmationService confirmationService) {
        this.service = service;
        this.confirmationService = confirmationService;
    }

    public List<EventListView> list(Authentication authentication) {
        return service.list(authentication);
    }

    public List<EventListView> forUser(String id) {
        return service.forUser(id);
    }

    public Try<EntityId> create(EventForm form, Authentication authentication) {
        return service.create(form, authentication);
    }

    public Try<EntityId> update(String id, EventForm form, Authentication authentication) {
        return service.update(id, form, authentication);
    }

    public Try<EventView> get(String id, Authentication authentication) {
        return service.get(id, authentication);
    }

    public Try<EntityId> delete(String id, Authentication authentication) {
        return service.delete(id, authentication);
    }

    public Try<EntityId> confirm(String id, Authentication authentication) {
        return confirmationService.confirm(id, authentication);
    }

    public Try<EntityId> decline(String id, Authentication authentication) {
        return confirmationService.decline(id, authentication);
    }
}