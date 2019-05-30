package com.psk.backend.domain.trip;

import com.psk.backend.domain.user.AuditUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
@Document
public class Trip {
    @Id
    private String id;

    private String name;

    private TripApartment source;

    private TripApartment destination;

    private List<TripUser> users;

    private TripStatus status;

    private LocalDateTime departure;

    private LocalDateTime arrival;

    private boolean reservation;

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
        return this.departure.plusDays(1).isAfter(other.getDeparture())
                && this.departure.minusDays(1).isBefore(other.getDeparture())
                && this.source.getId().equals(other.getSource().getId())
                && this.destination.getId().equals(other.getDestination().getId())
                && this.status.equals(TripStatus.DRAFT)
                && !hasSameUsers(other);
    }

    private boolean hasSameUsers(Trip other) {
        return this.users.stream().map(TripUser::getId)
                .anyMatch(id -> other.getUsers().stream().map(TripUser::getId).collect(toList()).contains(id));
    }

    public Trip merge(Trip other) {
        this.users.addAll(other.getUsers());

        if (this.getReservationBegin() == null && this.getReservationEnd() == null) {
            this.setReservationBegin(other.getReservationBegin());
            this.setReservationEnd(other.getReservationEnd());
        }

        if (this.getFlight() != null) {
            if (other.getFlight() != null) {
                this.getFlight().merge(other.getFlight());
            }
        } else {
            this.setFlight(other.getFlight());
        }

        if (this.getHotel() != null) {
            if (other.getHotel() != null) {
                this.getHotel().merge(other.getHotel());
            }
        } else {
            this.setHotel(other.getHotel());
        }

        if (this.getCarRent() != null) {
            if (this.getCarRent() != null) {
                this.getCarRent().merge(other.getCarRent());
            }
        } else {
            this.setCarRent(other.getCarRent());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("First trip description:\n")
                .append(this.getDescription())
                .append("\nSecond trip description:\n")
                .append(other.getDescription());
        this.setDescription(builder.toString());
        return this;
    }

    public Long getUserInApartmentCount() {
        return getUsers().stream().filter(TripUser::isInApartment).filter(TripUser::notDeclined).count();
    }

    public boolean hasReservation() {
        return this.reservation;
    }

    public void reservationAssigned() {
        this.reservation = true;
        this.arrival = null;
    }
}
