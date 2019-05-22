package com.psk.backend.trip;

import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripListView;
import com.psk.backend.trip.value.TripView;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TripControllerService {
    private final TripRepository repository;
    private final CreateTripService service;

    public TripControllerService(TripRepository repository, CreateTripService service) {
        this.repository = repository;
        this.service = service;
    }

    public Page<TripListView> list(Pageable page) {
        return repository.list(page);
    }

    public Try<EntityId> create(TripForm form) {
        return service.create(form);
    }

    public Try<EntityId> update(String id, TripForm form) {
        return repository.update(id, form);
    }

    public Try<TripView> get(String id) {
        return repository.get(id);
    }

    public Try<EntityId> delete(String id) {
        return repository.delete(id);
    }

    public Try<EntityId> confirm(String id, String userId) { return repository.updateStatus(id, userId, TripUserStatus.CONFIRMED);}

    public Try<EntityId> decline(String id, String userId) { return repository.updateStatus(id, userId, TripUserStatus.DECLINED);}
}
