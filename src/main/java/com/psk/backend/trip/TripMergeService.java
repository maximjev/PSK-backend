package com.psk.backend.trip;

import com.psk.backend.apartment.reservation.ReservationRepository;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.TripMergeForm;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.MERGE_ERROR;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static io.atlassian.fugue.extensions.step.Steps.begin;

@Service
public class TripMergeService {

    private final TripRepository tripRepository;
    private final ReservationRepository reservationRepository;

    public TripMergeService(TripRepository tripRepository, ReservationRepository reservationRepository) {
        this.tripRepository = tripRepository;
        this.reservationRepository = reservationRepository;
    }

    public Try<EntityId> merge(TripMergeForm form) {
        return begin(tripRepository.findById(form.getTripOne()))
                .then(t1 -> tripRepository.findById(form.getTripTwo()))
                .then((t1, t2) -> {
                    if (!t1.isMergeableWith(t2)) {
                        return failure(MERGE_ERROR.entity(t1.getId(), t2.getId()));
                    }
                    t1.merge(t2);

                    if (t1.hasReservation() && t2.hasReservation()) {
                        reservationRepository.updatePlacesByTripId(t1.getId(), t1.getUserInApartmentCount())
                                .flatMap(res -> reservationRepository.deleteByTripId(t2.getId()));
                    } else if (!t1.hasReservation() && t2.hasReservation()) {
                        reservationRepository.reassignTripByTripId(t2.getId(), t1.getId());
                        t1.reservationAssigned();
                    }

                    tripRepository.save(t1);
                    return successful(entityId(t1.getId()));
                })
                .then((t1, t2, entityId) -> tripRepository.delete(t2.getId()))
                .yield((t1, t2, entityId, id) -> entityId);
    }
}
