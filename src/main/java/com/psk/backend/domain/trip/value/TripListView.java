package com.psk.backend.domain.trip.value;

import com.psk.backend.domain.common.address.AddressView;
import com.psk.backend.domain.trip.TripStatus;
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
    private LocalDateTime departure;

    private LocalDateTime createdAt;
}
