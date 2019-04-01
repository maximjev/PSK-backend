package com.psk.backend.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document
@ToString
@Getter
@Setter
public class User {

    public enum UserRole {
        ROLE_USER, ROLE_ADMIN, ROLE_ORGANIZER
    }

    public enum UserStatus {
        VERIFICATION_PENDING, ACTIVE
    }

    @Id
    private String id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserRole role;
    private UserStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}
