package com.psk.backend.domain.user.value;

import com.psk.backend.domain.user.UserRole;
import com.psk.backend.domain.user.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserView {
    private String id;
    private String name;
    private String surname;
    private String email;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
