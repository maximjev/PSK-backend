package com.psk.backend.facade;

import com.psk.backend.repository.ReservationRepository;
import com.psk.backend.domain.reservation.value.PlacementFilter;
import com.psk.backend.domain.reservation.value.PlacementResult;
import com.psk.backend.domain.reservation.value.ReservationListView;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReservationControllerService {

    private final ReservationRepository repository;

    public ReservationControllerService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Page<ReservationListView> reservations(String apartmentId, Pageable page) {
        return repository.list(apartmentId, page);
    }

    public Try<PlacementResult> availablePlaces(String id, PlacementFilter filter) {
        return repository.availablePlaces(id, filter);
    }
}
