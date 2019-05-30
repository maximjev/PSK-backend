package com.psk.backend.controller;

import com.psk.backend.domain.reservation.value.PlacementFilter;
import com.psk.backend.domain.reservation.value.PlacementResult;
import com.psk.backend.domain.reservation.value.ReservationListView;
import com.psk.backend.domain.common.CommonErrors;
import com.psk.backend.facade.ReservationControllerService;
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
@RequestMapping("/apartment/{id}/reservation")
public class ReservationController {

    @Autowired
    private final ReservationControllerService service;

    public ReservationController(ReservationControllerService service) {
        this.service = service;
    }

    @CommonErrors
    @ApiOperation(value = "Get apartment reservation list", response = ReservationListView.class)
    @GetMapping
    public Page<ReservationListView> getAllReservations(
            @PathVariable("id") String apartmentId,
            Pageable page) {
        return service.reservations(apartmentId, page);
    }

    @ApiOperation(value = "Get available places of apartment", response = PlacementResult.class)
    @GetMapping("/places")
    @CommonErrors
    public ResponseEntity<?> getPlaces(@PathVariable("id") String id, PlacementFilter filter) {
        return service.availablePlaces(id, filter).fold(e -> unprocessableEntity().body(e), ResponseEntity::ok);
    }
}
