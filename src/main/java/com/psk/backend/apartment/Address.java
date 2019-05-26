package com.psk.backend.apartment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Address {
    private String city;
    private String street;
    private String apartmentNumber;
}
