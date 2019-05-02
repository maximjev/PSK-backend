package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.PasswordForm;
import io.atlassian.fugue.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

import java.util.Optional;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.*;
import static com.psk.backend.user.UserStatus.ACTIVE;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;


@Service
public class CreateUserService {

    @Resource
    protected PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final ConfirmationKeyRepository keyRepository;
    private final EmailService emailService;
    @Value("${app.url}")
    private String url;

    public CreateUserService(UserRepository userRepository, EmailService emailService, ConfirmationKeyRepository keyRepository) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.keyRepository = keyRepository;
    }

    // TODO: insert user to db, create confirmation key and send it to user email
    public Try<EntityId> create(NewUserForm form) {
        if (userRepository.findByUsername(form.getEmail()).isPresent())
            return failure(USER_EXISTS.entity(User.class.getName(), form.getEmail()));
        userRepository.insert(form);
        User user = userRepository.findByUsername(form.getEmail()).get();
        resetPassword(user.getEmail());
        return successful(entityId(user.getId()));
    }

    public Try<EntityId> savePassword(PasswordForm form) {
        if (keyRepository.getByToken(form.getToken()).isEmpty())
            return failure(INVALID_TOKEN.entity(form.getToken()));
        ConfirmationKey key = keyRepository.getByToken(form.getToken()).get();
        if (!key.isValid())
            return failure(INVALID_TOKEN.entity(key.getToken()));
        User user = userRepository.getById(key.getId()).get();
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setStatus(ACTIVE);
        userRepository.save(user);
        keyRepository.invalidate(form.getToken());
        return successful(entityId(user.getId()));
    }


    public Try<EntityId> resetPassword(String email) {
        Optional<User> user = userRepository.findByUsername(email);
        if (user.isEmpty()) return failure(USER_NOT_FOUND.entity(email));
        ConfirmationKey key = new ConfirmationKey(user.get().getId());
        keyRepository.save(key);
        emailService.send(emailService.constructResetTokenEmail(url, key.getToken(), user.get()));
        return successful(entityId(user.get().getId()));
    }


}
