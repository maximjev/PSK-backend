package com.psk.backend.service;

import com.psk.backend.domain.reservation.ReservationMapper;
import com.psk.backend.domain.reservation.ReservationRepository;
import com.psk.backend.domain.reservation.value.PlacementFilter;
import com.psk.backend.domain.reservation.value.PlacementResult;
import com.psk.backend.domain.calendar.EventRepository;
import com.psk.backend.domain.common.EntityId;
import com.psk.backend.domain.trip.TripRepository;
import com.psk.backend.domain.trip.value.TripCreateForm;
import com.psk.backend.domain.trip.value.TripForm;
import com.psk.backend.domain.trip.value.TripUserForm;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;

import static com.psk.backend.domain.common.Error.UNEXPECTED_ERROR;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;

@Service
public class TripManagementService {

    private final TripRepository tripRepository;
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final ReservationMapper reservationMapper;

    public TripManagementService(TripRepository tripRepository,
                                 ReservationRepository reservationRepository,
                                 EventRepository eventRepository,
                                 ReservationMapper reservationMapper) {
        this.tripRepository = tripRepository;
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.reservationMapper = reservationMapper;
    }

    public Try<EntityId> create(TripCreateForm form) {
        return reservationRepository
                .availablePlaces(form.getDestination(),
                        new PlacementFilter(form.getReservationBegin(), form.getReservationEnd()))
                .flatMap(a -> validateReservation(a, form))
                .flatMap(a -> tripRepository.insert(form))
                .flatMap(entityId ->
                        form.isReservation()
                                ? reservationRepository.insert(reservationMapper.fromTrip(form), entityId.getId()).map(r -> entityId)
                                : successful(entityId)
                );
    }

    public Try<EntityId> update(String id, TripForm form) {
        return tripRepository.findById(id).flatMap(t -> reservationRepository.availablePlaces(t.getDestination().getId(),
                new PlacementFilter(form.getReservationBegin(), form.getReservationEnd())))
                .flatMap(a -> validateReservation(a, form))
                .flatMap(a -> tripRepository.update(id, form))
                .flatMap(entityId -> tripRepository.findById(id).flatMap(t ->
                        t.isReservation()
                                ? reservationRepository.updateByTripId(id, reservationMapper.fromTrip(form)).map(r -> entityId)
                                : successful(entityId)));
    }

    public Try<EntityId> delete(String tripId) {
        return tripRepository.findById(tripId)
                .flatMap(t -> t.hasReservation()
                        ? reservationRepository.deleteByTripId(tripId).flatMap(r -> tripRepository.delete(tripId))
                        : tripRepository.delete(tripId)
                );
    }

    private Try<PlacementResult> validateReservation(PlacementResult result, TripForm form) {
        if ((result.getAvailablePlaces() >= getUserInApartmentCount(form)) || !form.isReservation()) {
            return successful(result);
        } else {
            return failure(UNEXPECTED_ERROR.entity("Not enough space in apartment"));
        }
    }

    private Long getUserInApartmentCount(TripForm form) {
        return form.getUsers()
                .stream()
                .filter(TripUserForm::isInApartment)
                .count();
    }
}
