package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.PasswordForm;
import io.atlassian.fugue.Try;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.USER_EXISTS;
import static com.psk.backend.common.Error.USER_NOT_FOUND;
import static com.psk.backend.user.UserStatus.ACTIVE;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;


@Service
public class CreateUserService {

    @Resource
    protected PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final ConfirmationKeyService confirmationKeyService;

    public CreateUserService(UserRepository userRepository, ConfirmationKeyService confirmationKeyService) {
        this.userRepository = userRepository;
        this.confirmationKeyService = confirmationKeyService;
    }

    // TODO: insert user to db, create confirmation key and send it to user email
    public Try<EntityId> create(NewUserForm form) {
        if (userRepository.findByUsername(form.getEmail()).isPresent()) return failure(USER_EXISTS.entity(User.class.getName(), form.getEmail()));
        userRepository.insert(form);
        User user = userRepository.findByUsername(form.getEmail()).get();
        confirmationKeyService.generateConfimationKey(user.getId());
        return successful(entityId(user.getId()));
    }
    public Try<EntityId> enterPassword(PasswordForm form){
        ConfirmationKey key = confirmationKeyService.getConfirmationKey(form.getKeyId());
        User user = userRepository.getById(key.getUserId()).get();
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setStatus(ACTIVE);
        userRepository.save(user);
        confirmationKeyService.invalidate(form.getKeyId());
        return successful(entityId(user.getId()));
    }
}
