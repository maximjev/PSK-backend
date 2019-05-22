package com.psk.backend.calendars;

import com.psk.backend.common.CommonErrors;
import com.psk.backend.common.EntityId;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.psk.backend.calendars.value.EventForm;
import com.psk.backend.calendars.value.EventListView;
import com.psk.backend.calendars.value.EventView;

@RestController
@RequestMapping("/calendars")
public class EventController {

    @Autowired
    private final EventControllerService service;

    public EventControllerService(EventControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get event list", response = EventListView.class)
    @GetMapping
    public Page<EventListView> getAll(Pageable page) {
        return service.list(page);
    }

    @ApiOperation(value = "Create event", response = EntityId.class)
    @CommonErrors
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventForm form) {
        return service.create(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get event", response = EventView.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return service.get(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Update event", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody EventForm form) {
        return service.update(id, form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Delete event", response = EntityId.class)
    @CommonErrors
    @DeleteMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id) {
        return service.delete(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
