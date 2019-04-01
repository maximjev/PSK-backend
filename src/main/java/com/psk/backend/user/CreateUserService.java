package com.psk.backend.user;

import com.psk.backend.common.EntityId;
import com.psk.backend.user.value.NewUserForm;
import io.atlassian.fugue.Try;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService {

    // TODO: insert user to db, create confirmation key and send it to user email
    public Try<EntityId> create(NewUserForm form) {
        return null;
    }
}
