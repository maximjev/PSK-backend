package com.psk.backend.calendars;

import java.time.LocalDateTime;
import java.util.List;

import com.psk.backend.user.AuditUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Event {

    @Id
    private String id;

    private String text;
	
	private List<EventUser> users;

    private LocalDateTime start;

    private LocalDateTime end;
	
	@CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;
}