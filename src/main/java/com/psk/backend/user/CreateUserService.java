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

    public Try<EntityId> create(NewUserForm form) {
        if (userRepository.findByUsername(form.getEmail()).isPresent())
            return failure(USER_CONFIRMATION_ERROR.entity(User.class.getName(), form.getEmail()));
        Try<EntityId> result = userRepository.insert(form);
        resetPassword(form.getEmail());
        return result;
    }

    public Try<EntityId> savePassword(PasswordForm form) {
        Optional<ConfirmationKey> key = keyRepository.getById(form.getToken());
        if (key.isEmpty() || !key.get().isValid())
            return failure(USER_CONFIRMATION_ERROR.entity(form.getToken()));
        return userRepository.findById(key.get().getUserId()).map(user -> {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
            user.setStatus(ACTIVE);
            userRepository.save(user);
            keyRepository.removeByUserId(user.getId());
            return entityId(user.getId());
        });
    }


    public Try<EntityId> resetPassword(String email) {
        return userRepository.findByEmail(email).map(user -> {
            ConfirmationKey key = new ConfirmationKey(user.getId());
            keyRepository.save(key);
            emailService.sendEmail("Password setup", constructResetTokenLink(url, key.getId()), email);
            return entityId(user.getId());
        });
    }

    private String constructResetTokenLink(
            String contextPath, String token) {
        return contextPath + "/user/changePassword?token=" + token;
    }

    public Try<EntityId> isValid(String token) {
        Try <ConfirmationKey> key = keyRepository.findById(token);
        if (key.isFailure()) return failure(USER_CONFIRMATION_ERROR.entity(token));
        return successful(entityId(token));
    }
}
