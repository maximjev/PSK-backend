package com.psk.backend.domain.trip

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

@Builder(builderStrategy = ExternalStrategy, forClass = TripUser)
class TripUserBuilder {
    TripUserBuilder() {
        id('123')
        name('name')
        surname('surname')
        email('email')
        status(TripUserStatus.CONFIRMED)
        inApartment(true)
        residenceAddress('address')
        carRent('car rent')
        flightTicket('flight ticket')
    }

    static TripUser tripUser() {
        new TripUserBuilder().build()
    }

    static TripUser tripUser(String id) {
        new TripUserBuilder(id: id).build()
    }
}
