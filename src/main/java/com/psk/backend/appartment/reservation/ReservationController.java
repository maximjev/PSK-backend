package com.psk.backend.appartment.reservation;

import com.psk.backend.appartment.reservation.value.PlacementFilter;
import com.psk.backend.appartment.reservation.value.PlacementResult;
import com.psk.backend.appartment.reservation.value.ReservationListView;
import com.psk.backend.common.CommonErrors;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.unprocessableEntity;

@RestController
@RequestMapping("/appartment/{appartmentId}/reservation")
public class ReservationController {

    @Autowired
    private final ReservationControllerService service;

    public ReservationController(ReservationControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get appartment reservation list", response = ReservationListView.class)
    @GetMapping
    public Page<ReservationListView> getAllReservations(
            @PathVariable("appartmentId") String appartmentId,
            Pageable page) {
        return service.reservations(appartmentId, page);
    }

    @ApiOperation(value = "Get available places of appartment", response = PlacementResult.class)
    @GetMapping("/places")
    @CommonErrors
    public ResponseEntity<?> getPlaces(@PathVariable("id") String id, PlacementFilter filter) {
        return service.availablePlaces(id, filter).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
