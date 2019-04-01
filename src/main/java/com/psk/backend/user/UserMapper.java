package com.psk.backend.user;

import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.UserListView;
import org.mapstruct.Mapping;

public abstract class UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", defaultValue = "CONFIRMATION_PENDING")
    public abstract User create(NewUserForm form);

    public abstract UserListView listView(User user);
}
