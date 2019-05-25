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

    private String name;

    private String source;

    private String destination;

    private List<TripUser> users;

    private TripStatus status;

    private LocalDateTime departion;

    private LocalDateTime arrival;

    private boolean noReservation;

    private LocalDateTime reservationBegin;

    private LocalDateTime reservationEnd;

    private String description;

    private Expenses flight;

    private Expenses hotel;

    private Expenses carRent;

    private BigDecimal otherExpenses;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;

    public boolean isMergeableWith(Trip other) {
        return this.departion.plusDays(1).isAfter(other.getDepartion())
                && this.departion.minusDays(1).isBefore(other.getDepartion())
                && this.source.equals(other.getDestination())
                && this.destination.equals(other.getDestination())
                && this.status.equals(TripStatus.DRAFT);
    }

    public Trip merge(Trip other) {
        this.users.addAll(other.getUsers());
        this.getFlight().merge(other.getFlight());
        this.getHotel().merge(other.getHotel());
        StringBuilder builder = new StringBuilder();
        builder.append("First trip description:\n")
                .append(this.getDescription())
                .append("\nSecond trip description:")
                .append(other.getDescription());
        this.setDescription(builder.toString());
        return this;
    }

    public Long getUserInAppartmentCount() {
        return getUsers().stream().filter(TripUser::isInAppartment).count();
    }

    public boolean hasReservation() {
        return !this.noReservation;
    }
}
