package com.psk.backend.trip;

import com.psk.backend.appartment.reservation.ReservationMapper;
import com.psk.backend.appartment.reservation.ReservationRepository;
import com.psk.backend.appartment.reservation.value.PlacementFilter;
import com.psk.backend.appartment.reservation.value.PlacementResult;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripUserForm;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;

import static com.psk.backend.common.Error.UNEXPECTED_ERROR;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;

@Service
public class CreateTripService {

    private final TripRepository tripRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public CreateTripService(TripRepository tripRepository,
                             ReservationRepository reservationRepository,
                             ReservationMapper reservationMapper) {
        this.tripRepository = tripRepository;
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    public Try<EntityId> create(TripForm form) {
        form.getUsers().stream().filter(TripUserForm::isInAppartment).count();
        return reservationRepository
                .availablePlaces(form.getDestination(),
                        new PlacementFilter(form.getReservationBegin(), form.getReservationEnd()))
                .flatMap(a -> validateReservation(a, form))
                .flatMap(a -> tripRepository.insert(form))
                .flatMap(entityId -> reservationRepository
                        .insert(reservationMapper.fromTrip(form).withTrip(entityId.getId()))
                        .map(r -> entityId));
    }

    public Try<EntityId> update(String id, TripForm form) {
        return reservationRepository
                .availablePlaces(form.getDestination(),
                        new PlacementFilter(form.getReservationBegin(), form.getReservationEnd()))
                .flatMap(a -> validateReservation(a, form))
                .flatMap(a -> tripRepository.update(id, form))
                .flatMap(entityId -> reservationRepository
                        .updateByTripId(id, reservationMapper.fromTrip(form).withTrip(entityId.getId()))
                        .map(r -> entityId));
    }

    private Try<PlacementResult> validateReservation(PlacementResult result, TripForm form) {
        if (result.getAvailablePlaces() <= form.getUsers().size()) {
            return failure(UNEXPECTED_ERROR.entity("Not enough space in appartment"));
        } else {
            return successful(result);
        }
    }
}
