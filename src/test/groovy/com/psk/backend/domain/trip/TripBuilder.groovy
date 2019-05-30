package com.psk.backend.domain.trip


import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

import static com.psk.backend.domain.trip.ExpensesBuilder.expenses
import static com.psk.backend.domain.trip.TripApartmentBuilder.tripApartment
import static com.psk.backend.domain.trip.TripUserBuilder.tripUser
import static java.time.LocalDateTime.of

@Builder(builderStrategy = ExternalStrategy, forClass = Trip)
class TripBuilder {
    TripBuilder() {
        id('123')
        name('name')
        source(tripApartment('ap-1'))
        destination(tripApartment('ap-2'))
        departure(of(2019, 7, 1, 12, 0))
        reservationBegin(of(2019, 7, 3, 12, 0))
        reservationEnd(of(2019, 7, 8, 12, 0))
        users([tripUser('1'), tripUser('2')])
        description('description')
        hotel(expenses())
        flight(expenses())
        otherExpenses(BigDecimal.valueOf(33.11))
    }

    static Trip trip() {
        new TripBuilder().build()
    }

    static Trip trip(String id) {
        new TripBuilder(
                id: id
        ).build()
    }

}
