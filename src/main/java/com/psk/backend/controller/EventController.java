package com.psk.backend.controller;

import com.psk.backend.domain.calendar.value.EventForm;
import com.psk.backend.domain.calendar.value.EventListView;
import com.psk.backend.domain.calendar.value.EventView;
import com.psk.backend.domain.common.CommonErrors;
import com.psk.backend.domain.common.EntityId;
import com.psk.backend.facade.EventControllerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.ResponseEntity.unprocessableEntity;

@RestController
@RequestMapping("/event")
public class EventController {

    @Autowired
    private final EventControllerService service;

    public EventController(EventControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get event list", response = EventListView.class)
    @GetMapping
    public List<EventListView> getAll(Authentication authentication) {
        return service.list(authentication);
    }

    @ApiOperation(value = "Create event", response = EntityId.class)
    @CommonErrors
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventForm form, Authentication authentication) {
        return service.create(form, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Update event", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody EventForm form, Authentication authentication) {
        return service.update(id, form, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Delete event", response = EntityId.class)
    @CommonErrors
    @DeleteMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, Authentication authentication) {
        return service.delete(id, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get event details for owner", response = EventView.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id, Authentication authentication) {
        return service.get(id, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get events for user", response = EventListView.class)
    @GetMapping("/user/{userId}")
    public List<EventListView> forUser(@PathVariable("userId") String userId) {
        return service.forUser(userId);
    }

    @ApiOperation(value = "Confirm event", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable("id") String id, Authentication authentication) {
        return service.confirm(id, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
    @ApiOperation(value = "Decline event", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}/decline")
    public ResponseEntity<?> decline(@PathVariable("id") String id, Authentication authentication) {
        return service.decline(id, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
