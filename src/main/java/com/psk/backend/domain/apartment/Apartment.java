package com.psk.backend.domain.apartment;


import com.psk.backend.domain.user.AuditUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document
@Getter
@Setter
public class Apartment {

    @Id
    private String id;

    private String name;

    private Address address;

    private Long size;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;
}
