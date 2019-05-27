package com.psk.backend.trip;

import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.*;
import com.psk.backend.user.UserRepository;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.psk.backend.common.Error.UNEXPECTED_ERROR;
import static io.atlassian.fugue.Try.failure;

@Service
public class TripControllerService {
    private final TripRepository repository;
    private final TripManagementService service;
    private final UserRepository userRepository;

    public TripControllerService(TripRepository repository, TripManagementService service, UserRepository userRepository) {
        this.repository = repository;
        this.service = service;
        this.userRepository = userRepository;
    }

    public Page<TripListView> list(Pageable page) {
        return repository.list(page);
    }

    public Try<EntityId> create(TripCreateForm form) {
        return service.create(form);
    }

    public Try<EntityId> update(String id, TripForm form) {
        return service.update(id, form);
    }

    public Try<TripView> get(String id) {
        return repository.get(id);
    }

    public Try<TripUserView> getUserView(String id, Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .flatMap(u -> repository.tripUserView(id, u.getId()));
    }

    public Try<EntityId> delete(String id) {
        return service.delete(id);
    }

    public Try<Page<TripListView>> match(String id, Pageable page) {
        return this.repository.match(id, page);
    }

    public Try<EntityId> merge(TripMergeForm form) {
        return service.merge(form);
    }

    public Try<EntityId> confirm(String id, String userId) {
        return repository.findById(id).flatMap(trip -> {
            if (trip.getDeparture().isAfter(LocalDateTime.now())) {
                return repository.updateStatus(id, userId, TripUserStatus.CONFIRMED);
            }
            return failure(UNEXPECTED_ERROR.entity(id));
        });
    }

    public Try<EntityId> decline(String id, String userId) {
        return repository.findById(id).flatMap(trip -> {
           if (trip.getDeparture().isAfter(LocalDateTime.now())) {
               repository.setUserApartmentReservation(id, userId, false);
               return repository.updateStatus(id, userId, TripUserStatus.DECLINED);
           }
           return failure(UNEXPECTED_ERROR.entity(id));
        });
    }

    public Page<TripListView> listByUser(Pageable page, String userId) {return repository.listByUser(page, userId); }
}
