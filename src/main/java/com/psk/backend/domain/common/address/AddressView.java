package com.psk.backend.domain.common.address;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressView {
    private String city;
    private String street;
    private String apartmentNumber;
}
