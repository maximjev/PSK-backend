package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.*;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.USER_NOT_FOUND;
import static io.atlassian.fugue.Try.failure;
import static io.atlassian.fugue.Try.successful;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class UserRepository {

    private final MongoOperations mongoOperations;
    private final UserMapper userMapper;

    public UserRepository(MongoOperations mongoOperations, UserMapper userMapper) {
        this.mongoOperations = mongoOperations;
        this.userMapper = userMapper;
    }


    public List<UserSelectView> all() {
        var conditions = Criteria.where("status").ne(UserStatus.VERIFICATION_PENDING);
        return mongoOperations.find(query(conditions), User.class)
                .stream()
                .map(userMapper::selectView)
                .collect(Collectors.toList());
    }

    public Page<UserListView> list(Pageable page, boolean active) {
        var conditions = new Criteria();

        if (active) {
            conditions = Criteria.where("status").ne(UserStatus.VERIFICATION_PENDING);
        }

        var total = mongoOperations.count(query(conditions), User.class);

        var users = mongoOperations.find(
                query(conditions)
                        .skip(page.getPageSize() * page.getPageNumber())
                        .limit(page.getPageSize()),
                User.class)
                .stream()
//                .sorted(comparing(User::getCreatedAt, reverseOrder()))
                .map(userMapper::listView)
                .collect(toList());

        return new PageImpl<>(users, page, total);
    }

    public Try<EntityId> insert(NewUserForm form) {
        User user = userMapper.create(form);

        mongoOperations.insert(user);
        return successful(entityId(user.getId()));
    }

    public Try<EntityId> save(User user) {
        mongoOperations.save(user);
        return successful(entityId(user.getId()));
    }

    public Optional<User> findByUsername(String username) {
        return ofNullable(mongoOperations.findOne(query(where("email").is(username)), User.class));
    }

    public Optional<User> getById(String id) {
        return ofNullable(mongoOperations.findOne(query(where("id").is(id)), User.class));
    }

    public Try<User> findById(String id) {
        return mongoOperations
                .query(User.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(USER_NOT_FOUND.entity(User.class.getName(), id)));
    }

    public Try<User> findByEmail(String email) {
        return mongoOperations
                .query(User.class)
                .matching(query(where("email").is(email)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(USER_NOT_FOUND.entity(email)));
    }

    public Try<EntityId> update(String userId, UpdateUserForm form) {
        return findById(userId).map(user -> {
            mongoOperations.save(userMapper.update(form, user));
            return entityId(user.getId());
        });
    }

    public Try<UserView> get(String id) {
        return findById(id).map(userMapper::view);
    }

}
