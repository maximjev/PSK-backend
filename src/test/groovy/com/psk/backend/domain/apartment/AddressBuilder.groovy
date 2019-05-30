package com.psk.backend.domain.apartment


import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

@Builder(builderStrategy = ExternalStrategy, forClass = Address)
class AddressBuilder {
    AddressBuilder() {
        city("Vilnius")
        street("Naugarduko")
        apartmentNumber("24")
    }

    static Address address() {
        new AddressBuilder().build()
    }
}
