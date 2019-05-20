package com.psk.backend.appartment;


import com.psk.backend.appartment.value.AppartmentForm;
import com.psk.backend.appartment.value.AppartmentListView;
import com.psk.backend.appartment.value.AppartmentView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AppartmentControllerService {

    private final AppartmentRepository appartmentRepository;

    public AppartmentControllerService(AppartmentRepository appartmentRepository) {
        this.appartmentRepository = appartmentRepository;
    }

    public Page<AppartmentListView> list(Pageable page) {
        return appartmentRepository.list(page);
    }

    public Try<EntityId> create(AppartmentForm form) {
        return appartmentRepository.insert(form);
    }

    public Try<EntityId> update(String id, AppartmentForm form) {
        return appartmentRepository.update(id, form);
    }

    public Try<AppartmentView> get(String id) {
        return appartmentRepository.get(id);
    }

    public Try<EntityId> delete(String id) {
        return appartmentRepository.delete(id);
    }
}
