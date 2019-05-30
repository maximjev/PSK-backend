package com.psk.backend.domain.reservation


import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Builder(builderStrategy = ExternalStrategy, forClass = Reservation)
class ReservationBuilder {
    ReservationBuilder() {
        apartmentId("123")
        from(LocalDateTime.of(2019, 1, 1, 12, 0))
        till(LocalDateTime.of(2019, 1, 9, 12, 0))
        places(2)
    }

    static Reservation reservation() {
        new ReservationBuilder().build()
    }

    static Reservation reservation(String id) {
        new ReservationBuilder().id(id).build()
    }

    static Reservation reservation(String id, String from, String till) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        new ReservationBuilder()
                .id(id)
                .from(LocalDateTime.parse(from, formatter))
                .till(LocalDateTime.parse(till, formatter))
                .build()
    }

    static Reservation reservation(String id, String from, String till, String aparmentId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        new ReservationBuilder()
                .id(id)
                .apartmentId(aparmentId)
                .from(LocalDateTime.parse(from, formatter))
                .till(LocalDateTime.parse(till, formatter))
                .build()
    }

    static Reservation reservation(String id, String from, String till, Long places) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        new ReservationBuilder()
                .id(id)
                .from(LocalDateTime.parse(from, formatter))
                .till(LocalDateTime.parse(till, formatter))
                .places(places)
                .build()
    }
}
