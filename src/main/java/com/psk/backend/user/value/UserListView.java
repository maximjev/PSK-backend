package com.psk.backend.user.value;

import com.psk.backend.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class UserListView {
    private String id;
    private String name;
    private String surname;
    private String email;
    private User.UserRole role;
    private LocalDateTime createdAt;
    private User.UserStatus status;
}
