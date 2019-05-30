package com.psk.backend.auth;

import com.psk.backend.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static java.util.Collections.emptyList;
import static java.util.List.of;


@Component
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> new User(
                        u.getEmail(),
                        u.getPassword(),
                        u.isActive(),
                        true,
                        true,
                        true,
                        u.getRole() != null ? of(new SimpleGrantedAuthority(u.getRole().toString())) : emptyList()
                )).orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found."));
    }
}
