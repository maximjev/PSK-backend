package com.psk.backend.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class AuditUser {

    private String userId;
    private String username;
    private String name;

    public AuditUser(String userId, String username, String name) {
        this.userId = userId;
        this.username = username;
        this.name = name;
    }

    public static AuditUser of(User user) {
        return new AuditUser(user.getId(), user.getEmail(), user.getName() + " " + user.getSurname());
    }
}
