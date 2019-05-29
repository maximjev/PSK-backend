package com.psk.backend.apartment.reservation;

import com.google.common.collect.Range;
import com.psk.backend.user.AuditUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@EqualsAndHashCode(of = { "id", "places", "from", "till", "apartmentId", "tripId"})
public class Reservation {

    @Id
    private String id;

    private Long places;

    private LocalDateTime from;

    private LocalDateTime till;

    private String apartmentId;

    private String tripId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;

    public Reservation withTrip(String tripId) {
        this.tripId = tripId;
        return this;
    }

    public boolean intersects(Reservation reservation) {
        return !reservation.equals(this) && Range.closed(reservation.getFrom(), reservation.getTill())
                .isConnected(Range.closed(this.from, this.till));
    }
}
