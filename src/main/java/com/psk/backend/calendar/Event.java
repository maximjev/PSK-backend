package com.psk.backend.calendar;

import com.psk.backend.user.AuditUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@Getter
@Setter
public class Event {

    @Id
    private String id;

    private String description;
	
	private List<EventUser> users = new ArrayList<>();

    private LocalDateTime start;

    private LocalDateTime end;

    private String tripId;
	
	@CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;

    public Event withTrip(String tripId) {
        this.tripId = tripId;
        return this;
    }
}