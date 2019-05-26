package com.psk.backend.trip;

import com.mongodb.client.result.DeleteResult;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.TripCreateForm;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripListView;
import com.psk.backend.trip.value.TripView;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.OBJECT_NOT_FOUND;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class TripRepository {


    private final MongoOperations mongoOperations;
    private final TripMapper mapper;

    public TripRepository(MongoOperations mongoOperations, TripMapper mapper) {
        this.mongoOperations = mongoOperations;
        this.mapper = mapper;
    }
    public List<Trip> getAllTrips(){
        return mongoOperations.findAll(Trip.class);
    }

    public Page<TripListView> list(Pageable page) {
        return listView(page, new Criteria());
    }

    public Try<Page<TripListView>> match(String id, Pageable page) {
        return findById(id).flatMap(t -> {
            Criteria criteria = new Criteria().orOperator(
                    where("departure").lte(t.getDeparture().plusDays(1)),
                    where("departure").gte(t.getDeparture().minusDays(1))
            ).andOperator(
                    where("status").is(TripStatus.DRAFT),
                    where("source").is(t.getSource()),
                    where("destination").is(t.getDestination()));

            return successful(listView(page, criteria));
        });
    }

    private Page<TripListView> listView(Pageable page, Criteria conditions) {
        var total = mongoOperations.count(query(conditions), Trip.class);

        var entities = mongoOperations.find(
                query(conditions)
                        .skip(page.getPageSize() * page.getPageNumber())
                        .limit(page.getPageSize()),
                Trip.class)
                .stream()
                .map(mapper::listView)
                .collect(toList());

        return new PageImpl<>(entities, page, total);
    }

    public Try<EntityId> insert(TripCreateForm form) {
        Trip entity = mapper.create(form);
        mongoOperations.insert(entity);
        return successful(entityId(entity.getId()));
    }

    public Try<EntityId> save(Trip trip) {
        return successful(entityId(mongoOperations.save(trip).getId()));
    }

    public Try<EntityId> update(String id, TripForm form) {
        return findById(id).map(a -> {
            mongoOperations.save(mapper.update(form, a));
            return entityId(a.getId());
        });
    }

    public Try<EntityId> delete(String id) {
        DeleteResult result = mongoOperations.remove(
                query(where("id").is(id)),
                Trip.class
        );
        return result.getDeletedCount() > 0
                ? successful(entityId(id))
                : failure(OBJECT_NOT_FOUND.entity(id));
    }

    public Try<Trip> findById(String id) {
        return mongoOperations
                .query(Trip.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Trip.class.getName(), id)));
    }

    public Try<TripView> get(String id) {
        return findById(id).map(mapper::view);
    }

    public Try<EntityId> updateStatus(String id, String userId, TripUserStatus status) {
        return findById(id).flatMap(trip -> {
            List <TripUser> users= trip.getUsers();
            users.stream()
                    .filter(u -> userId.equals(u.getId()))
                    .forEach(u -> u.setStatus(status));
            mongoOperations.save(trip);
            return successful(entityId(id));

        });
    }

    public Page<TripListView> listByUser(Pageable page, String userId) {
        var conditions = new Criteria();

        var total = mongoOperations.count(query(conditions)
                .addCriteria(where("users").elemMatch(Criteria.where("id").is(userId))),
                Trip.class);

        var entities = mongoOperations.find(
                query(conditions)
                        .addCriteria(where("users").elemMatch(Criteria.where("id").is(userId)))
                        .skip(page.getPageSize() * page.getPageNumber())
                        .limit(page.getPageSize()),
                Trip.class)
                .stream()
                .map(mapper::listView)
                .collect(toList());

        return new PageImpl<>(entities, page, total);
    }
}
