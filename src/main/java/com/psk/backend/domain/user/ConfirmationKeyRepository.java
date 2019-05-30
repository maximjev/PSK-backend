package com.psk.backend.domain.user;

import com.psk.backend.domain.common.EntityId;

import io.atlassian.fugue.Try;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.psk.backend.domain.common.EntityId.entityId;
import static com.psk.backend.domain.common.Error.USER_CONFIRMATION_ERROR;
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

    public Optional<ConfirmationKey> getById(String id) {
        return ofNullable(mongoOperations.findOne(query(where("id").is(id)), ConfirmationKey.class));
    }

    public Try<EntityId> remove(ConfirmationKey key){
                mongoOperations.remove(new Query(Criteria.where("userId").is(key.getUserId())), ConfirmationKey.class);
                return successful(entityId(key.getId()));
    }
    public Try<EntityId> save(ConfirmationKey key) {
        mongoOperations.save(key);
        return successful(entityId(key.getId()));
    }
    public Try<ConfirmationKey> findById(String id) {
        return mongoOperations
                .query(ConfirmationKey.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(USER_CONFIRMATION_ERROR.entity(ConfirmationKey.class.getName(), id)));
    }
}
