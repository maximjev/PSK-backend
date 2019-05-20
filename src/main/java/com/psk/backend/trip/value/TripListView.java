package com.psk.backend.trip.value;

import com.psk.backend.common.address.AddressView;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripListView {
    private String id;
    private AddressView sourceAddress;
    private AddressView destinationAddress;
    private LocalDateTime departion;
    private LocalDateTime reservationBegin;
    private LocalDateTime reservationEnd;
}
