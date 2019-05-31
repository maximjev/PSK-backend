package com.psk.backend.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class User {

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

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;


    public boolean isActive() {
        return UserStatus.ACTIVE == status;
    }
}
