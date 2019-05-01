package com.psk.backend.user;

import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.user.value.NewUserForm;
import com.psk.backend.user.value.UserListView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;


import javax.annotation.Resource;

@Mapper(config = BaseMapperConfig.class)
public abstract class UserMapper {

    @Resource
    protected PasswordEncoder passwordEncoder;

    // TODO: before verification is implemented, password is same as username
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(form.getEmail()))")
    @Mapping(target = "status", expression = "java(UserStatus.VERIFICATION_PENDING)")
    public abstract User create(NewUserForm form);

    public abstract UserListView listView(User user);
}
