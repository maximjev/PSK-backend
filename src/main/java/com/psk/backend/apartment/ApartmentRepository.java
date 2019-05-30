package com.psk.backend.apartment;

import com.mongodb.client.result.DeleteResult;
import com.psk.backend.apartment.value.ApartmentForm;
import com.psk.backend.apartment.value.ApartmentListView;
import com.psk.backend.apartment.value.ApartmentSelectView;
import com.psk.backend.apartment.value.ApartmentView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.OBJECT_NOT_FOUND;
import static com.psk.backend.common.Error.OPTIMISTIC_LOCKING;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ApartmentRepository {

    private final MongoOperations mongoOperations;
    private final ApartmentMapper apartmentMapper;

    public ApartmentRepository(MongoOperations mongoOperations, ApartmentMapper apartmentMapper) {
        this.mongoOperations = mongoOperations;
        this.apartmentMapper = apartmentMapper;
    }

    public List<ApartmentSelectView> all() {
        return mongoOperations.findAll(Apartment.class)
                .stream()
                .map(apartmentMapper::selectView)
                .collect(Collectors.toList());
    }

    public Page<ApartmentListView> list(Pageable page) {
        var conditions = new Criteria();

        var total = mongoOperations.count(query(conditions), Apartment.class);

        var apartments = mongoOperations.find(
                query(conditions)
                        .skip(page.getPageSize() * page.getPageNumber())
                        .limit(page.getPageSize()),
                Apartment.class)
                .stream()
                .sorted(comparing(Apartment::getCreatedAt, reverseOrder()))
                .map(apartmentMapper::listView)
                .collect(toList());

        return new PageImpl<>(apartments, page, total);
    }

    public Try<EntityId> insert(ApartmentForm form) {
        Apartment apartment = apartmentMapper.create(form);
        mongoOperations.insert(apartment);
        return successful(entityId(apartment.getId()));
    }

    public Try<EntityId> update(String id, ApartmentForm form) {
        return findById(id).flatMap(a -> {
            if (!a.getUpdatedAt().equals(form.getUpdatedAt())) {
                return failure(OPTIMISTIC_LOCKING.entity(id));
            }
            mongoOperations.save(apartmentMapper.update(form, a));
            return successful(entityId(a.getId()));
        });
    }

    public Try<EntityId> delete(String id) {
        DeleteResult result = mongoOperations.remove(
                query(where("id").is(id)),
                Apartment.class
        );
        return result.getDeletedCount() > 0
                ? successful(entityId(id))
                : failure(OBJECT_NOT_FOUND.entity(Apartment.class.getName(), id));
    }

    public Try<Apartment> findById(String id) {
        return mongoOperations
                .query(Apartment.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Apartment.class.getName(), id)));
    }

    public Try<ApartmentView> get(String id) {
        return findById(id).map(apartmentMapper::view);
    }
}
