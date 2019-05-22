package com.psk.backend.calendars;

import com.mongodb.client.result.DeleteResult;
import com.psk.backend.common.EntityId;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import com.psk.backend.calendars.value.EventForm;
import com.psk.backend.calendars.value.EventListView;
import com.psk.backend.calendars.value.EventView;

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

    public Page<EventListView> list(Pageable page) {
        var conditions = new Criteria();

        var total = mongoOperations.count(query(conditions), Event.class);

        var events = mongoOperations.find(
                query(conditions)
                        .skip(page.getOffset())
                        .limit(page.getPageSize()),
                Event.class)
                .stream()
                .map(eventMapper::listView)
                .collect(toList());

        return new PageImpl<>(events, page, total);
    }

    public Try<EntityId> insert(EventForm form) {
        Event event = eventMapper.create(form);
        mongoOperations.insert(event);
        return successful(entityId(event.getId()));
    }

    public Try<EntityId> update(String id, EventForm form) {
        return findById(id).map(a -> {
            mongoOperations.saveeventMapper.update(form, a));
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

    public Try<EventView> get(String id) {
        return findById(id).map(eventMapper::view);
    }
}