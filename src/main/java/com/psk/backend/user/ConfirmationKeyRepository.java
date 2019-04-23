package com.psk.backend.user;

import com.psk.backend.common.EntityId;

import io.atlassian.fugue.Try;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.psk.backend.common.EntityId.entityId;
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
    public Try<EntityId> invalidate(String keyId){
       ConfirmationKey key = getById(keyId).get();
       key.setValid(false);
       mongoOperations.save(key);
       return successful(entityId(key.getId()));
    }
}
