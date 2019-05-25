package com.psk.backend.trip;

import com.psk.backend.appartment.reservation.ReservationMapper;
import com.psk.backend.appartment.reservation.ReservationRepository;
import com.psk.backend.appartment.reservation.value.PlacementFilter;
import com.psk.backend.appartment.reservation.value.PlacementResult;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripMergeForm;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;

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

    public Try<EntityId> delete(String tripId) {
        return reservationRepository.deleteByTripId(tripId)
                .flatMap(id -> tripRepository.delete(tripId));
    }

    private Try<PlacementResult> validateReservation(PlacementResult result, TripForm form) {
        if (result.getAvailablePlaces() <= form.getUserInAppartmentCount()) {
            return failure(UNEXPECTED_ERROR.entity("Not enough space in appartment"));
        } else {
            return successful(result);
        }
    }

    public Try<EntityId> merge(TripMergeForm form) {
        return begin(tripRepository.findById(form.getTripOne()))
                .then(t1 -> tripRepository.findById(form.getTripTwo()))
                .then((t1, t2) -> t1.isMergeableWith(t2)
                        ? tripRepository.save(t1.merge(t2))
                        : failure(MERGE_ERROR.entity(t1.getId(), t2.getId())))
                .then((t1, t2, tripId) -> reservationRepository.updatePlacesByTripId(t1.getId(), t1.getUserInAppartmentCount()))
                .then((t1, t2, tripId, res1Id) -> reservationRepository.deleteByTripId(t2.getId()))
                .then((t1, t2, tripId, res1Id, res2Id) -> tripRepository.delete(t2.getId()))
                .yield((t1, t2, t1Id, res1Id, res2Id, t2Id) -> t1Id);
    }
}
