package com.psk.backend.user;

import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.UserListView;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public abstract class UserMapper {

    public abstract User create(NewUserForm form);

    public abstract UserListView listView(User user);
}
