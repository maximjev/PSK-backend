package com.psk.backend.controller;


import com.psk.backend.facade.ApartmentControllerService;
import com.psk.backend.domain.apartment.value.ApartmentForm;
import com.psk.backend.domain.apartment.value.ApartmentListView;
import com.psk.backend.domain.apartment.value.ApartmentSelectView;
import com.psk.backend.domain.apartment.value.ApartmentView;
import com.psk.backend.domain.common.CommonErrors;
import com.psk.backend.domain.common.EntityId;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.ResponseEntity.unprocessableEntity;

@RestController
@RequestMapping("/apartment")
public class ApartmentController {

    @Autowired
    private final ApartmentControllerService service;

    public ApartmentController(ApartmentControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get paged apartment list", response = ApartmentListView.class)
    @GetMapping
    public Page<ApartmentListView> getAll(Pageable page) {
        return service.list(page);
    }

    @CommonErrors
    @ApiOperation(value = "Get apartment list", response = ApartmentSelectView.class)
    @GetMapping("/all")
    public List<ApartmentSelectView> all() {
        return service.all();
    }

    @ApiOperation(value = "Create apartment", response = EntityId.class)
    @CommonErrors
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ApartmentForm form) {
        return service.create(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get apartment", response = ApartmentView.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return service.get(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Update apartment", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody ApartmentForm form) {
        return service.update(id, form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Delete apartment", response = EntityId.class)
    @CommonErrors
    @DeleteMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id) {
        return service.delete(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
