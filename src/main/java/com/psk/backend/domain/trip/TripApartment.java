package com.psk.backend.domain.trip;

import com.psk.backend.domain.apartment.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TripApartment {
    private String id;
    private Address address;

    public TripApartment(String id, Address address) {
        this.id = id;
        this.address = address;
    }

    public TripApartment(String id) {
        this.id = id;
    }
}
