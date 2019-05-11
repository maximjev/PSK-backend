package com.psk.backend.appartment;


import com.psk.backend.appartment.value.AppartmentForm;
import com.psk.backend.appartment.value.AppartmentListView;
import com.psk.backend.common.CommonErrors;
import com.psk.backend.common.EntityId;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.unprocessableEntity;

@RestController
@RequestMapping("/appartment")
public class AppartmentController {

    @Autowired
    private final AppartmentControllerService service;

    public AppartmentController(AppartmentControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get paged appartment list", response = AppartmentListView.class)
    @GetMapping
    public Page<AppartmentListView> getAll(Pageable page) {
        return service.appartments(page);
    }

    @ApiOperation(value = "Create appartment", response = EntityId.class)
    @CommonErrors
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody AppartmentForm form) {
        return service.create(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Update appartment", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody AppartmentForm form) {
        return service.update(id, form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Delete appartment", response = EntityId.class)
    @CommonErrors
    @DeleteMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id) {
        return service.delete(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
