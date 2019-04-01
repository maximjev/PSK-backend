package com.psk.backend.user;

import com.psk.backend.common.CommonErrors;
import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.UserListView;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.unprocessableEntity;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserControllerService service;

    public UserController(UserControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @GetMapping
    public Page<UserListView> getAll(Pageable page) {
        return service.users(page);
    }

    @ApiOperation(value = "Create user", response = EntityId.class)
    @CommonErrors
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody NewUserForm form) {
        return service.create(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
