package com.psk.backend.trip.value;

import com.psk.backend.common.address.AddressView;
import com.psk.backend.trip.TripStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripListView {
    private String id;
    private String name;
    private TripStatus status;
    private AddressView sourceAddress;
    private AddressView destinationAddress;
    private LocalDateTime departion;
    private LocalDateTime reservationBegin;
    private LocalDateTime reservationEnd;
}
