package com.psk.backend.facade;


import com.psk.backend.repository.ApartmentRepository;
import com.psk.backend.repository.ReservationRepository;
import com.psk.backend.domain.apartment.value.ApartmentForm;
import com.psk.backend.domain.apartment.value.ApartmentListView;
import com.psk.backend.domain.apartment.value.ApartmentSelectView;
import com.psk.backend.domain.apartment.value.ApartmentView;
import com.psk.backend.domain.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.psk.backend.domain.common.Error.OPERATION_NOT_ALLOWED;
import static io.atlassian.fugue.Try.failure;

@Service
public class ApartmentControllerService {

    private final ApartmentRepository apartmentRepository;

    private final ReservationRepository reservationRepository;

    public ApartmentControllerService(ApartmentRepository apartmentRepository, ReservationRepository reservationRepository) {
        this.apartmentRepository = apartmentRepository;
        this.reservationRepository = reservationRepository;
    }

    public Page<ApartmentListView> list(Pageable page) {
        return apartmentRepository.list(page);
    }

    public List<ApartmentSelectView> all() {
        return apartmentRepository.all();
    }

    public Try<EntityId> create(ApartmentForm form) {
        return apartmentRepository.insert(form);
    }

    public Try<EntityId> update(String id, ApartmentForm form) {
        if (reservationRepository.calculateApartmentMaxPlace(id) > form.getSize()) {
            return failure(OPERATION_NOT_ALLOWED.entity("The apartment has reservations that take more places than provided size."));
        }
        return apartmentRepository.update(id, form);
    }

    public Try<ApartmentView> get(String id) {
        return apartmentRepository.get(id);
    }

    public Try<EntityId> delete(String id) {
        if (reservationRepository.futureApartmentReservations(id).size() > 0) {
            return failure(OPERATION_NOT_ALLOWED.entity("The apartment has current or future reservations."));
        }
        return apartmentRepository.delete(id);
    }
}
