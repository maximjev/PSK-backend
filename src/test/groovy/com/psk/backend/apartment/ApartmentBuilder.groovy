package com.psk.backend.apartment

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy


@Builder(builderStrategy = ExternalStrategy, forClass = Apartment)
class ApartmentBuilder {
    ApartmentBuilder(){
        address(AddressBuilder.address())
        size(6)
    }

    static Apartment apartment() {
        new ApartmentBuilder().build()
    }

    static Apartment apartment(String id) {
        new ApartmentBuilder().id(id).build()
    }
}
