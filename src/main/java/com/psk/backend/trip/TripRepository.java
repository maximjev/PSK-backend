package com.psk.backend.trip;

import com.mongodb.client.result.DeleteResult;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.common.EntityId;
import com.psk.backend.trip.value.*;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.OBJECT_NOT_FOUND;
import static com.psk.backend.common.Error.OPTIMISTIC_LOCKING;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
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

    public List<EventListView> eventListView(String userId) {
        var conditions = new Criteria().andOperator(
                where("users").elemMatch(where("id").is(userId)
                        .orOperator(
                                where("status").is(TripUserStatus.CONFIRMATION_PENDING),
                                where("status").is(TripUserStatus.CONFIRMED)
                        )),
                where("departure").gt(LocalDateTime.now().minusMonths(1)),
                where("status").ne(TripStatus.CANCELLED)
        );

        return mongoOperations.find(
                query(conditions),
                Trip.class)
                .stream()
                .map(mapper::toEvent)
                .collect(toList());
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
                    new Criteria().orOperator(
                            where("status").is(TripStatus.DRAFT),
                            where("status").is(TripStatus.CONFIRMED)
                    ),
                    where("source").is(t.getSource()),
                    where("destination").is(t.getDestination()),
                    where("id").ne(id),
                    where("users").elemMatch(where("id").nin(t.getUsers().stream().map(TripUser::getId).collect(toList()))));

            return successful(listView(page, criteria));
        });
    }

    public Try<TripUserView> tripUserView(String id, String userId) {
        return findById(id).flatMap(t -> successful(mapper.tripUserView(t, userId)));
    }

    private Page<TripListView> listView(Pageable page, Criteria conditions) {
        var total = mongoOperations.count(query(conditions), Trip.class);

        var entities = mongoOperations.find(
                query(conditions)
                        .skip(page.getPageSize() * page.getPageNumber())
                        .limit(page.getPageSize()),
                Trip.class)
                .stream()
                .sorted(comparing(Trip::getCreatedAt, reverseOrder()))
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
        return findById(id).flatMap(a -> {
            if (!a.getUpdatedAt().equals(form.getUpdatedAt())) {
                return failure(OPTIMISTIC_LOCKING.entity(a.getId()));
            }
            mongoOperations.save(mapper.update(form, a));
            return successful(entityId(a.getId()));
        });
    }

    public Try<EntityId> delete(String id) {
        DeleteResult result = mongoOperations.remove(
                query(where("id").is(id)),
                Trip.class
        );
        return result.getDeletedCount() > 0
                ? successful(entityId(id))
                : failure(OBJECT_NOT_FOUND.entity(Trip.class.getName(), id));
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
            List<TripUser> users = trip.getUsers();
            users.stream()
                    .filter(u -> userId.equals(u.getId()))
                    .forEach(u -> u.setStatus(status));
            mongoOperations.save(trip);
            return successful(entityId(id));
        });
    }

    public Try<EntityId> setUserApartmentReservation(String id, String userId, boolean status) {
        return findById(id).flatMap(trip -> {
            List<TripUser> users = trip.getUsers();
            users.stream()
                    .filter(u -> userId.equals(u.getId()))
                    .forEach(u -> u.setInApartment(status));
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

    public List<Trip> getTripsByStatus(TripStatus status) {
        return mongoOperations.find(new Query(Criteria.where("status").is(status)), Trip.class);
    }
}
