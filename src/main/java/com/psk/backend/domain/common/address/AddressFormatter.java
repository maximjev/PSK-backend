package com.psk.backend.domain.common.address;

import com.psk.backend.domain.apartment.Address;

public class AddressFormatter {

    public static String formatAddress(Address address) {
        return String.format("%s %s, %s", address.getStreet(), address.getApartmentNumber(), address.getCity());
    }
}
