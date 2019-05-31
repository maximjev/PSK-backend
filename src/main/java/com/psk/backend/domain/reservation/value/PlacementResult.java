package com.psk.backend.domain.reservation.value;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacementResult {
    private Long takenPlaces;
    private Long reservations;
    private Long availablePlaces;

    public void calculateAvailablePlaces(Long size) {
        availablePlaces = size - takenPlaces >= 0 ? size - takenPlaces : 0;
    }

    public PlacementResult(Long takenPlaces, Long reservations) {
        this.takenPlaces = takenPlaces;
        this.reservations = reservations;
    }
}
