package com.psk.backend.auth;

import com.psk.backend.auth.value.CurrentUserView;
import com.psk.backend.mapper.UserMapper;
import com.psk.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/me")
public class MeController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public MeController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<CurrentUserView> get(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .map(userMapper::fromUser)
                .map(ResponseEntity::ok)
                .orElse(notFound().build());
    }
}
