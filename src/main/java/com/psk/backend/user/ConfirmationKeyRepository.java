package com.psk.backend.user;

import com.psk.backend.common.EntityId;

import io.atlassian.fugue.Try;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.INVALID_TOKEN;
import static com.psk.backend.common.Error.USER_NOT_FOUND;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.Optional.ofNullable;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ConfirmationKeyRepository {
    private final MongoOperations mongoOperations;

    public ConfirmationKeyRepository(MongoOperations mongoOperations){
        this.mongoOperations = mongoOperations;
    }
    public Try<EntityId> insert(ConfirmationKey key) {
        mongoOperations.insert(key);
        return successful(entityId(key.getId()));
    }
    public Optional<ConfirmationKey> getById(String id) {
        return ofNullable(mongoOperations.findOne(query(where("id").is(id)), ConfirmationKey.class));
    }
    public Optional<ConfirmationKey> getByToken(String token) {
        return ofNullable(mongoOperations.findOne(query(where("token").is(token)), ConfirmationKey.class));
    }
    public Try<EntityId> invalidate(String token){
        return findByToken(token).map(key -> {
            key.setValid(false);
            mongoOperations.save(key);
            return entityId(key.getId());});
    }
    public Try<EntityId> save(ConfirmationKey key) {
        mongoOperations.save(key);
        return successful(entityId(key.getId()));
    }
    public Try<ConfirmationKey> findByToken(String token) {
        return mongoOperations
                .query(ConfirmationKey.class)
                .matching(query(where("token").is(token)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(INVALID_TOKEN.entity(ConfirmationKey.class.getName(), token)));
    }
}
