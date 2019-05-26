package com.psk.backend.trip

import com.psk.backend.apartment.AddressBuilder
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
