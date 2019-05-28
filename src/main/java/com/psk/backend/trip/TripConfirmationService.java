package com.psk.backend.trip;

import com.psk.backend.apartment.reservation.ReservationRepository;
import com.psk.backend.calendar.EventRepository;
import com.psk.backend.common.EntityId;
import com.psk.backend.user.UserRepository;
import io.atlassian.fugue.Try;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.psk.backend.common.Error.UNEXPECTED_ERROR;
import static io.atlassian.fugue.Try.failure;
import static java.time.LocalDateTime.now;

@Service
public class TripConfirmationService {


    private final TripRepository repository;
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public TripConfirmationService(TripRepository tripRepository,
                                   ReservationRepository reservationRepository,
                                   EventRepository eventRepository,
                                   UserRepository userRepository) {
        this.repository = tripRepository;
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }


    public Try<EntityId> confirm(String id, Authentication authentication) {
        return updateStatus(id, authentication, TripUserStatus.CONFIRMED);
    }

    public Try<EntityId> decline(String id, Authentication authentication) {
        return updateStatus(id, authentication, TripUserStatus.DECLINED);
    }

    private Try<EntityId> updateStatus(String id, Authentication authentication, TripUserStatus status) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(u -> repository.findById(id).flatMap(t -> {
                    if (t.getDeparture().isAfter(now())) {
                        var entityId = repository.updateStatus(id, u.getId(), status);
                        if (t.isReservation()) {
                            reservationRepository.updatePlacesByTripId(t.getId(), t.getUserInApartmentCount());
                        }
                        return entityId;
                    } else {
                        return failure(UNEXPECTED_ERROR.entity(id));
                    }
                }));
    }
}
