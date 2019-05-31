package com.psk.backend.mapper;

import com.psk.backend.domain.calendar.Event;
import com.psk.backend.domain.calendar.EventUser;
import com.psk.backend.domain.calendar.value.EventForm;
import com.psk.backend.domain.calendar.value.EventListView;
import com.psk.backend.domain.calendar.value.EventUserView;
import com.psk.backend.domain.calendar.value.EventView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.domain.user.User;
import com.psk.backend.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.annotation.Resource;

@Mapper(config = BaseMapperConfig.class)
public abstract class EventMapper {

    @Resource
    private UserRepository userRepository;

    public abstract Event create(EventForm form);

    @Mapping(target = "trip", expression = "java(false)")
    @Mapping(target = "owner", expression = "java(e.getCreatedBy().getUserId().equals(userId))")
    @Mapping(target = "userStatus", expression = "java(e.getUsers().stream()" +
            ".filter(u -> u.getId().equals(userId)).findFirst().get().getStatus())")
    public abstract EventListView listView(Event e, String userId);

    public abstract Event update(EventForm form, @MappingTarget Event event);

    public EventUser user(String id) {
        return userRepository
                .findById(id).map(this::user)
                .getOrElse(null);
    }

    @Mapping(target = "status", expression = "java(com.psk.backend.domain.calendar.EventUserStatus.CONFIRMATION_PENDING)")
    public abstract EventUser user(User user);

    public abstract EventView view(Event event);

    public abstract EventUserView userView(EventUser user);
}
