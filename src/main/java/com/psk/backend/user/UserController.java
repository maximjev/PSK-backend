package com.psk.backend.user;

import com.psk.backend.common.CommonErrors;
import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.*;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserControllerService service;

    public UserController(UserControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get paged user list", response = UserListView.class)
    @GetMapping
    public Page<UserListView> getAll(Pageable page) {
        return service.users(page);
    }


    @CommonErrors
    @ApiOperation(value = "Get user list", response = UserSelectView.class)
    @GetMapping("/all")
    public List<UserSelectView> all() {
        return service.all();
    }

    @ApiOperation(value = "Create user", response = EntityId.class)
    @CommonErrors
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody NewUserForm form) {
        return service.create(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Get user", response = UserView.class)
    @GetMapping("/details/{id}")
    public ResponseEntity<?> get(@PathVariable("id") String id) {
        return service.get(id).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Update user's details", response = EntityId.class)
    @CommonErrors
    @PutMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable String userId, @Valid @RequestBody UpdateUserForm form) {
        return service.update(userId, form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }

    @ApiOperation(value = "Reset password", response = EntityId.class)
    @CommonErrors
    @PostMapping("/resetPassword")
    public  ResponseEntity<?> changeUserPassword(@RequestParam("email") String userEmail) {
        return service.resetPassword(userEmail).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);

    }
    @ApiOperation(value = "Save password", response = EntityId.class)
    @CommonErrors
    @PostMapping("/savePassword")
    public ResponseEntity<?> update(@Valid @RequestBody PasswordForm form) {
        return service.savePassword(form).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
    @ApiOperation(value = "Validate token", response = EntityId.class)
    @CommonErrors
    @GetMapping("/token/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        return service.isValid(token).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }


}
