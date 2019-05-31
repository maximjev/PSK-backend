package com.psk.backend.domain.user.value;

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
    private String role;
    private LocalDateTime createdAt;
    private String status;
}
