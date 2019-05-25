package com.psk.backend.trip;

import com.psk.backend.appartment.reservation.ReservationMapper;
import com.psk.backend.appartment.reservation.ReservationRepository;
import com.psk.backend.appartment.reservation.value.PlacementFilter;
import com.psk.backend.appartment.reservation.value.PlacementResult;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripMergeForm;
import com.psk.backend.trip.value.TripUserForm;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.MERGE_ERROR;
import static com.psk.backend.common.Error.UNEXPECTED_ERROR;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static io.atlassian.fugue.extensions.step.Steps.begin;

@Service
public class TripManagementService {

    private final TripRepository tripRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public TripManagementService(TripRepository tripRepository,
                                 ReservationRepository reservationRepository,
                                 ReservationMapper reservationMapper) {
        this.tripRepository = tripRepository;
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    public Try<EntityId> create(TripForm form) {
        return reservationRepository
                .availablePlaces(form.getDestination(),
                        new PlacementFilter(form.getReservationBegin(), form.getReservationEnd()))
                .flatMap(a -> validateReservation(a, form))
                .flatMap(a -> tripRepository.insert(form))
                .flatMap(entityId ->
                        form.isNoReservation()
                                ? successful(entityId)
                                : reservationRepository.insert(reservationMapper.fromTrip(form)
                                .withTrip(entityId.getId())).map(r -> entityId)
                );
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

    public Try<EntityId> delete(String tripId) {
        return reservationRepository.deleteByTripId(tripId)
                .flatMap(id -> tripRepository.delete(tripId));
    }

    private Try<PlacementResult> validateReservation(PlacementResult result, TripForm form) {
        if ((result.getAvailablePlaces() > getUserInAppartmentCount(form)) || form.isNoReservation()) {
            return successful(result);
        } else {
            return failure(UNEXPECTED_ERROR.entity("Not enough space in appartment"));
        }
    }

    private Long getUserInAppartmentCount(TripForm form) {
        return form.getUsers()
                .stream()
                .filter(TripUserForm::isInAppartment)
                .count();
    }

    public Try<EntityId> merge(TripMergeForm form) {
        return begin(tripRepository.findById(form.getTripOne()))
                .then(t1 -> tripRepository.findById(form.getTripTwo()))
                .then((t1, t2) -> {
                    if (t1.isMergeableWith(t2)) {
                        tripRepository.save(t1.merge(t2));
                    } else {
                        return failure(MERGE_ERROR.entity(t1.getId(), t2.getId()));
                    }

                    if (t1.hasReservation() && t2.hasReservation()) {
                        reservationRepository.updatePlacesByTripId(t1.getId(), t2.getUserInAppartmentCount())
                                .flatMap(res -> reservationRepository.deleteByTripId(t2.getId()));
                    } else if (!t1.hasReservation() && t2.hasReservation()) {
                        reservationRepository.reassignTripByTripId(t2.getId(), t1.getId());
                    }
                    return successful(entityId(t1.getId()));
                })
                .then((t1, t2, entityId) -> tripRepository.delete(t2.getId()))
                .yield((t1, t2, entityId, id) -> entityId);
    }
}
