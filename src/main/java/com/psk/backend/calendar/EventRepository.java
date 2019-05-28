package com.psk.backend.calendar;

import com.mongodb.client.result.DeleteResult;
import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.calendar.value.EventView;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.OBJECT_NOT_FOUND;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class EventRepository {

    private final MongoOperations mongoOperations;
    private final EventMapper eventMapper;

    public EventRepository(MongoOperations mongoOperations, EventMapper eventMapper) {
        this.mongoOperations = mongoOperations;
        this.eventMapper = eventMapper;
    }

    public List<EventListView> list(String userId) {
        var conditions = new Criteria().andOperator(
                where("users").elemMatch(where("id").is(userId)),
                where("from").gt(LocalDateTime.now().minusMonths(1))
        );

        return mongoOperations.find(
                query(conditions),
                Event.class)
                .stream()
                .map(eventMapper::listView)
                .collect(toList());
    }

    public Try<EntityId> insert(EventForm form) {
        Event event = eventMapper.create(form);
        mongoOperations.insert(event);
        return successful(entityId(event.getId()));
    }
    public Try<EntityId> insert(EventForm form, String tripId) {
        var entity = mongoOperations.insert(eventMapper.create(form).withTrip(tripId));
        return successful(entityId(entity.getId()));
    }

    public Try<EntityId> update(String id, EventForm form) {
        return findByTripId(id).map(a -> {
            mongoOperations.save(eventMapper.update(form, a));
            return entityId(a.getId());
        });
    }

    public Try<EntityId> delete(String id) {
        DeleteResult result = mongoOperations.remove(
                query(where("id").is(id)),
                Event.class
        );
        return result.getDeletedCount() > 0
                ? successful(entityId(id))
                : failure(OBJECT_NOT_FOUND.entity(id));
    }

    public Try<Event> findById(String id) {
        return mongoOperations
                .query(Event.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Event.class.getName(), id)));
    }
    public Try<Event> findByTripId(String tripId) {
        return mongoOperations
                .query(Event.class)
                .matching(query(where("tripId").is(tripId)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Event.class.getName(), tripId)));
    }

    public Try<EventView> get(String id) {
        return findById(id).map(eventMapper::view);
    }
}