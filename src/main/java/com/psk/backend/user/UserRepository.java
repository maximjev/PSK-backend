package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.UserListView;
import io.atlassian.fugue.Try;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.psk.backend.common.EntityId.entityId;
import static com.psk.backend.common.Error.USER_NOT_FOUND;
import static com.psk.backend.user.UserRole.ROLE_ORGANIZER;
import static com.psk.backend.user.UserRole.ROLE_USER;
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


    public Page<UserListView> list(Pageable page) {
        var conditions = new Criteria().orOperator(where("role").is(ROLE_USER), where("role").is(ROLE_ORGANIZER));

        var total = mongoOperations.count(query(conditions), User.class);

        var users = mongoOperations.find(
                query(conditions)
                        .skip(page.getOffset())
                        .limit(page.getPageSize()),
                User.class)
                .stream()
                .map(userMapper::listView)
                .collect(toList());

        return new PageImpl<>(users, page, total);
    }

    public Try<EntityId> insert(NewUserForm form) {
        User user = userMapper.create(form);
        mongoOperations.insert(user);
        return successful(entityId(user.getId()));
    }

    public Optional<User> findByUsername(String username) {
        return ofNullable(mongoOperations.findOne(query(where("email").is(username)), User.class));
    }

    public Try<User> findById(String id) {
        return mongoOperations
                .query(User.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(Try::successful)
                .orElseGet(() -> failure(USER_NOT_FOUND.entity(User.class.getName(), id)));
    }
}
