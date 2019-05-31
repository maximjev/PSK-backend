package com.psk.backend.service;

import com.psk.backend.domain.common.EntityId;
import com.psk.backend.domain.common.Mail;
import com.psk.backend.domain.user.ConfirmationKey;
import com.psk.backend.domain.user.value.NewUserForm;
import com.psk.backend.domain.user.value.PasswordForm;
import com.psk.backend.repository.ConfirmationKeyRepository;
import com.psk.backend.repository.UserRepository;
import io.atlassian.fugue.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.psk.backend.domain.common.EntityId.entityId;
import static com.psk.backend.domain.common.Error.USER_CONFIRMATION_ERROR;
import static com.psk.backend.domain.user.UserStatus.ACTIVE;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;


@Service
public class CreateUserService {

    @Resource
    protected PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final ConfirmationKeyRepository keyRepository;
    private final EmailService emailService;

    private final SpringTemplateEngine templateEngine;

    @Value("${app.url}")
    private String url;

    public CreateUserService(UserRepository userRepository,
                             ConfirmationKeyRepository keyRepository,
                             EmailService emailService,
                             SpringTemplateEngine templateEngine) {
        this.userRepository = userRepository;
        this.keyRepository = keyRepository;
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    public Try<EntityId> create(NewUserForm form) {
        if (userRepository.findByUsername(form.getEmail()).isPresent())
            return failure(USER_CONFIRMATION_ERROR.entity(form.getEmail()));
        userRepository.insert(form);
        return resetPassword(form.getEmail());
    }

    public Try<EntityId> savePassword(PasswordForm form) {
        Optional<ConfirmationKey> key = keyRepository.getById(form.getToken());
        if (key.isEmpty() || !key.get().isValid())
            return failure(USER_CONFIRMATION_ERROR.entity(form.getToken()));

        return userRepository.findById(key.get().getUserId()).map(user -> {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
            user.setStatus(ACTIVE);
            userRepository.save(user);
            keyRepository.remove(key.get());
            return entityId(user.getId());
        });
    }


    public Try<EntityId> resetPassword(String email) {
        return userRepository.findByEmail(email).map(user -> {
            ConfirmationKey key = new ConfirmationKey(user.getId());
            keyRepository.save(key);

            Mail mail = new Mail();
            mail.setTo(email);
            mail.setSubject("Password setup");

            Map<String, Object> model = new HashMap<>();
            model.put("name", user.getName());
            model.put("surname", user.getSurname());
            model.put("url", constructResetTokenLink(url, key.getId()));
            mail.setModel(model);

            emailService.sendEmail(mail);
            return entityId(user.getId());
        });
    }

    private String constructResetTokenLink(
            String contextPath, String token) {
        return contextPath + "/user/changePassword?token=" + token;
    }

    public Try<EntityId> isTokenValid(String token) {
        Optional<ConfirmationKey> key = keyRepository.getById(token);
        if (key.isEmpty() || !key.get().isValid()) {
            return failure(USER_CONFIRMATION_ERROR.entity(token));
        }
        return successful(entityId(token));
    }
}
