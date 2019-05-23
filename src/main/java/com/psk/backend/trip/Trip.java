package com.psk.backend.trip;

import com.psk.backend.user.AuditUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Document
public class Trip {
    @Id
    private String id;

    private String source;

    private String destination;

    private List<TripUser> users;

    private TripStatus status;

    private LocalDateTime departion;

    private LocalDateTime reservationBegin;

    private LocalDateTime reservationEnd;

    private String description;

    private Expenses flight;

    private Expenses hotel;

    private BigDecimal otherExpenses;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;
}
