package com.psk.backend.controller;

import com.psk.backend.domain.common.CommonErrors;
import com.psk.backend.domain.common.EntityId;
import com.psk.backend.facade.TripControllerService;
import com.psk.backend.domain.trip.value.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.unprocessableEntity;

@RestController
@RequestMapping("trip")
public class TripController {

    private final TripControllerService service;

    public TripController(TripControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get paged trip list", response = TripListView.class)
    @GetMapping
    public Page<TripListView> getAll(Pageable page) {
        return service.list(page);
    }

    @ApiOperation(value = "Create trip", response = EntityId.class)
    @CommonErrors
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TripCreateForm form) {
        return service.create(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get trip", response = TripView.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return service.get(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get user trip view", response = TripUserView.class)
    @GetMapping("/{id}/user-view")
    public ResponseEntity<?> getUserView(@PathVariable("id") String id, Authentication authentication) {
        return service.getUserView(id, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Update trip", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody TripForm form) {
        return service.update(id, form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Delete trip", response = EntityId.class)
    @CommonErrors
    @DeleteMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id) {
        return service.delete(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Confirm trip", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirm(@PathVariable("id") String id, Authentication authentication) {
        return service.confirm(id, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
    @ApiOperation(value = "Decline trip", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}/decline")
    public ResponseEntity<?> decline(@PathVariable("id") String id, Authentication authentication) {
        return service.decline(id, authentication).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @CommonErrors
    @ApiOperation(value = "Get paged user's trip list", response = TripListView.class)
    @GetMapping("/user")
    public Page<TripListView> listByUser(Pageable page, Authentication authentication) {
        return service.listByUser(page, authentication);
    }


    @ApiOperation(value = "Get paged mergable trips", response = TripListView.class)
    @CommonErrors
    @GetMapping("/{id}/match")
    public ResponseEntity<?> match(@PathVariable("id") String id, Pageable page) {
        return service.match(id, page).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Merge trips", response = EntityId.class)
    @CommonErrors
    @PostMapping("/merge")
    public ResponseEntity<?> merge(TripMergeForm form) {
        return service.merge(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
