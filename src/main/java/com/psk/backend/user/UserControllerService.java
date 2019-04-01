package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.UserListView;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserControllerService {

    private final CreateUserService createUserService;
    private final UserRepository userRepository;

    public UserControllerService(CreateUserService createUserService, UserRepository userRepository) {
        this.createUserService = createUserService;
        this.userRepository = userRepository;
    }

    public Page<UserListView> users(Pageable page) {
        return userRepository.list(page);
    }

    public Try<EntityId> create(NewUserForm form) {
        return createUserService.create(form);
    }
}
