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
        Try<EntityId> result = userRepository.insert(form);
        resetPassword(form.getEmail());
        return result;
    }

    public Try<EntityId> savePassword(PasswordForm form) {
        Optional<ConfirmationKey> key = keyRepository.getById(form.getToken());
        if (key.isEmpty() || !key.get().isValid() || !form.getUserID().equals(key.get().getUserId()))
            return failure(INVALID_TOKEN.entity(form.getToken()));
        Optional<User> user = userRepository.getById(form.getUserID());
        if (user.isEmpty())
            return failure(USER_NOT_FOUND.entity(form.getUserID()));
        user.get().setPassword(passwordEncoder.encode(form.getPassword()));
        user.get().setStatus(ACTIVE);
        userRepository.save(user.get());
        keyRepository.removeByUserId(form.getUserID());
        return successful(entityId(form.getUserID()));
    }


    public Try<EntityId> resetPassword(String email) {
        Optional<User> user = userRepository.findByUsername(email);
        if (user.isEmpty()) return failure(USER_NOT_FOUND.entity(email));
        ConfirmationKey key = new ConfirmationKey(user.get().getId());
        keyRepository.save(key);
        emailService.sendEmail("Password setup", constructResetTokenLink(url, key.getId(), user.get()), email);
        return successful(entityId(user.get().getId()));
    }

    private String constructResetTokenLink(
            String contextPath, String token, User user) {
        return contextPath + "/user/changePassword?userId=" + user.getId() + "&token=" + token;
    }


}
