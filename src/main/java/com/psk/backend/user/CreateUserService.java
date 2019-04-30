package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.PasswordForm;
import io.atlassian.fugue.Try;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

import java.util.Optional;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.UNEXPECTED_ERROR;
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
    private final EmailService emailService;

    public CreateUserService(UserRepository userRepository, ConfirmationKeyService confirmationKeyService, EmailService emailService) {
        this.userRepository = userRepository;
        this.confirmationKeyService = confirmationKeyService;
        this.emailService = emailService;
    }

    // TODO: insert user to db, create confirmation key and send it to user email
    public Try<EntityId> create(NewUserForm form) {
        if (userRepository.findByUsername(form.getEmail()).isPresent())
            return failure(USER_EXISTS.entity(User.class.getName(), form.getEmail()));
        userRepository.insert(form);
        User user = userRepository.findByUsername(form.getEmail()).get();
        resetPassword(user.getEmail());
        return successful(entityId("User created"));
    }

    public Try<EntityId> savePassword(PasswordForm form) {
        if (confirmationKeyService.getConfirmationKeyByToken(form.getToken()).isEmpty())
            return failure(UNEXPECTED_ERROR.entity("invalid token"));
        ConfirmationKey key = confirmationKeyService.getConfirmationKeyByToken(form.getToken()).get();
        if (!confirmationKeyService.validatePasswordResetToken(key))
            return failure(UNEXPECTED_ERROR.entity("invalid token"));
        User user = userRepository.getById(key.getId()).get();
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setStatus(ACTIVE);
        userRepository.save(user);
        confirmationKeyService.invalidate(form.getToken());
        return successful(entityId(user.getId()));
    }


    public Try<EntityId> resetPassword(String email) {
        Optional<User> user = userRepository.findByUsername(email);
        if (user.isEmpty()) return failure(USER_NOT_FOUND.entity(email));
        ConfirmationKey key = confirmationKeyService.generateConfirmationKey(user.get().getId());
        emailService.send(emailService.constructResetTokenEmail("localhost:8000", key.getToken(), user.get()));
        return successful(entityId(user.get().getId()));
    }


}
