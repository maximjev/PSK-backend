package com.psk.backend.calendar;

import com.mongodb.client.result.DeleteResult;
import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.calendar.value.EventView;
import com.psk.backend.common.EntityId;
import com.psk.backend.user.AuditUser;
import com.psk.backend.user.User;
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
                where("start").gt(LocalDateTime.now().minusMonths(1)),
                where("users").elemMatch(where("id").is(userId)
                        .orOperator(
                                where("status").is(EventUserStatus.CONFIRMATION_PENDING),
                                where("status").is(EventUserStatus.CONFIRMED)
                        ))
        );

        return mongoOperations.find(
                query(conditions),
                Event.class)
                .stream()
                .map((Event e) -> eventMapper.listView(e, userId))
                .collect(toList());
    }

    public Try<EntityId> insert(EventForm form, User user) {
        Event event = eventMapper.create(form);
        var eventUser = eventMapper.user(user);
        eventUser.setStatus(EventUserStatus.CONFIRMED);
        if (event.getUsers() == null) {
            event.setUsers(List.of(eventUser));
        } else {
            event.getUsers().add(eventUser);
        }
        mongoOperations.insert(event);
        return successful(entityId(event.getId()));
    }

    public Try<EntityId> update(String id, EventForm form, AuditUser user) {
        return this.findByCriteria(id, eventOfOwner(id, user))
                .map(e -> {
                    mongoOperations.save(eventMapper.update(form, e));
                    return entityId(id);
                });
    }

    private Criteria eventOfOwner(String id, AuditUser user) {
        return new Criteria().andOperator(
                where("id").is(id),
                where("createdBy").is(user)
        );
    }

    public Try<EntityId> delete(String id, AuditUser user) {
        DeleteResult result = mongoOperations.remove(
                query(eventOfOwner(id, user)),
                Event.class
        );
        return result.getDeletedCount() > 0
                ? successful(entityId(id))
                : failure(OBJECT_NOT_FOUND.entity(Event.class.getName(), id));
    }

    public Try<Event> findByCriteria(String id, Criteria criteria) {
        return mongoOperations
                .query(Event.class)
                .matching(query(criteria))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(OBJECT_NOT_FOUND.entity(Event.class.getName(), id)));
    }

    public Try<EntityId> updateStatus(String id, String userId, EventUserStatus status) {
        return findByCriteria(id, where("id").is(id)).map(e -> {
            e.getUsers().stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst()
                    .ifPresent(u -> u.setStatus(status));
            mongoOperations.save(e);
            return entityId(e.getId());
        });
    }

    public Try<EventView> get(String id, AuditUser user) {
        return findByCriteria(id, eventOfOwner(id, user))
                .map(eventMapper::view);
    }
}