package com.psk.backend.apartment;


import com.psk.backend.apartment.value.ApartmentForm;
import com.psk.backend.apartment.value.ApartmentListView;
import com.psk.backend.apartment.value.ApartmentSelectView;
import com.psk.backend.apartment.value.ApartmentView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApartmentControllerService {

    private final ApartmentRepository apartmentRepository;

    public ApartmentControllerService(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
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
        return apartmentRepository.update(id, form);
    }

    public Try<ApartmentView> get(String id) {
        return apartmentRepository.get(id);
    }

    public Try<EntityId> delete(String id) {
        return apartmentRepository.delete(id);
    }
}
