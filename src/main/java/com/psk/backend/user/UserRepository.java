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

import static com.psk.backend.user.User.UserRole.ROLE_ORGANIZER;
import static com.psk.backend.user.User.UserRole.ROLE_USER;
import static com.psk.backend.user.User.UserStatus.ACTIVE;
import static io.atlassian.fugue.Try.successful;
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
        var conditions = new Criteria()
                .orOperator(where("role").is(ROLE_USER), where("role").is(ROLE_ORGANIZER))
                .andOperator(where("status").is(ACTIVE));

        var total = mongoOperations.count(query(conditions), User.class);

        var users = mongoOperations.find(
                query(conditions)
                        .skip(page.getOffset())
                        .limit(page.getPageSize()),
                User.class)
                .stream()
                .map(userMapper::listView)
                .collect(toList());

        return new PageImpl<UserListView>(users, page, total);
    }

    public Try<EntityId> insert(NewUserForm form) {
        User user = userMapper.create(form);
        mongoOperations.insert(user);
        return successful(EntityId.entityId(user.getId()));
    }
}
