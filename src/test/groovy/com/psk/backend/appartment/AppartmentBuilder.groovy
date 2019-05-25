package com.psk.backend.appartment

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy


@Builder(builderStrategy = ExternalStrategy, forClass = Appartment)
class AppartmentBuilder {
    AppartmentBuilder(){
        address(AddressBuilder.address())
        size(6)
    }

    static Appartment appartment() {
        new AppartmentBuilder().build()
    }

    static Appartment appartment(String id) {
        new AppartmentBuilder().id(id).build()
    }
}
