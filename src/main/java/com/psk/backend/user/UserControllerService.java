package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.*;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserControllerService {

    private final CreateUserService createUserService;
    private final UserRepository userRepository;

    public UserControllerService(CreateUserService createUserService, UserRepository userRepository) {
        this.createUserService = createUserService;
        this.userRepository = userRepository;
    }

    public Page<UserListView> users(Pageable page, boolean active) {
        return userRepository.list(page, active);
    }

    public List<UserSelectView> all() {
        return userRepository.all();
    }

    public Try<EntityId> create(NewUserForm form) {
        return createUserService.create(form);
    }
    public Try<EntityId> update(String userId, UpdateUserForm form) { return userRepository.update(userId, form); }
    public Try<EntityId> savePassword(PasswordForm form) { return createUserService.savePassword(form); }
    public Try<EntityId> resetPassword(String email) { return createUserService.resetPassword(email); }
    public Try<EntityId> isValid(String token) { return createUserService.isTokenValid(token); }

    public Try<UserView> get(String id) { return userRepository.get(id);}
}
