package com.psk.backend.appartment.reservation;

import com.psk.backend.user.AuditUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
public class Reservation {

    @Id
    private String id;

    private Long places;

    private LocalDateTime from;

    private LocalDateTime till;

    private String appartmentId;

    private String tripId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private AuditUser createdBy;

    @LastModifiedBy
    private AuditUser updatedBy;

    public static Builder builder() {
        return new Reservation().new Builder();
    }

    public class Builder {
        private Builder() {}

        public Builder withPlaces(Long places) {
            Reservation.this.places = places;
            return this;
        }

        public Builder withAppartment(String appartment) {
            Reservation.this.appartmentId = appartmentId;
            return this;
        }

        public Builder from(LocalDateTime from) {
            Reservation.this.from = from;
            return this;
        }

        public Builder till(LocalDateTime till) {
            Reservation.this.till = till;
            return this;
        }

        public Builder withTrip(String trip) {
            Reservation.this.tripId = trip;
            return this;
        }

        public Reservation build() {
            return Reservation.this;
        }
    }
}
