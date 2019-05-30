package com.psk.backend.domain.trip

import com.psk.backend.domain.apartment.AddressBuilder
import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

@Builder(builderStrategy = ExternalStrategy, forClass = TripApartment)
class TripApartmentBuilder {
    TripApartmentBuilder() {
        address(AddressBuilder.address())
    }

    static TripApartment tripApartment(String id) {
        new TripApartmentBuilder().id(id).build()
    }
}
