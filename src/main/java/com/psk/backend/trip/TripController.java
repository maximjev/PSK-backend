package com.psk.backend.trip;

import com.psk.backend.common.CommonErrors;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripListView;
import com.psk.backend.trip.value.TripView;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> create(@Valid @RequestBody TripForm form) {
        return service.create(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get trip", response = TripView.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return service.get(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
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
    @PutMapping("/{id}/{userId}/confirm/")
    public ResponseEntity<?> confirm(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return service.confirm(id, userId).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
    @ApiOperation(value = "Decline trip", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}/{userId}/decline/")
    public ResponseEntity<?> decline(@PathVariable("id") String id, @PathVariable("userId") String userId) {
        return service.decline(id, userId).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @CommonErrors
    @ApiOperation(value = "Get paged user's trip list", response = TripListView.class)
    @GetMapping("/user/{userId}")
    public Page<TripListView> getByUser(Pageable page, @PathVariable("userId") String userId) { return service.listByUser(page, userId); }

}
