package com.psk.backend.config;

import com.psk.backend.user.AuditUser;
import com.psk.backend.user.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableMongoAuditing
@Configuration
public class MongoConfig {
    public static class SpringSecurityAuditorAware implements AuditorAware<AuditUser> {

        private final UserRepository userRepository;

        public SpringSecurityAuditorAware(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public Optional<AuditUser> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            } else {
                return getAuditUser(authentication);
            }
        }

        public Optional<AuditUser> getAuditUser(Authentication authentication) {
            Optional<AuditUser> auditUser;
            auditUser = userRepository.findByUsername(authentication.getName())
                    .map(AuditUser::of);
            return auditUser;
        }
    }

}
