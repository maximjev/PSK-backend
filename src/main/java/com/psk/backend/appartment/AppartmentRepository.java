package com.psk.backend.appartment;

import com.mongodb.client.result.DeleteResult;
import com.psk.backend.appartment.value.AppartmentForm;
import com.psk.backend.appartment.value.AppartmentListView;
import com.psk.backend.appartment.value.AppartmentView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.OBJECT_NOT_FOUND;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class AppartmentRepository {

    private final MongoOperations mongoOperations;
    private final AppartmentMapper appartmentMapper;

    public AppartmentRepository(MongoOperations mongoOperations, AppartmentMapper appartmentMapper) {
        this.mongoOperations = mongoOperations;
        this.appartmentMapper = appartmentMapper;
    }

    public Page<AppartmentListView> list(Pageable page) {
        var conditions = new Criteria();

        var total = mongoOperations.count(query(conditions), Appartment.class);

        var appartments = mongoOperations.find(
                query(conditions)
                        .skip(page.getOffset())
                        .limit(page.getPageSize()),
                Appartment.class)
                .stream()
                .map(appartmentMapper::listView)
                .collect(toList());

        return new PageImpl<>(appartments, page, total);
    }

    public Try<EntityId> insert(AppartmentForm form) {
        Appartment appartment = appartmentMapper.create(form);
        mongoOperations.insert(appartment);
        return successful(entityId(appartment.getId()));
    }

    public Try<EntityId> update(String id, AppartmentForm form) {
        return findById(id).map(a -> {
            mongoOperations.save(appartmentMapper.update(form, a));
            return entityId(a.getId());
        });
    }

    public Try<EntityId> delete(String id) {
        DeleteResult result = mongoOperations.remove(
                query(where("id").is(id)),
                Appartment.class
        );
        return result.getDeletedCount() > 0
                ? successful(entityId(id))
                : failure(OBJECT_NOT_FOUND.entity(id));
    }

    public Try<Appartment> findById(String id) {
        return mongoOperations
                .query(Appartment.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Appartment.class.getName(), id)));
    }

    public Try<AppartmentView> get(String id) {
        return findById(id).map(appartmentMapper::view);
    }
}
