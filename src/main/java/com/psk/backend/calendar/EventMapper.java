package com.psk.backend.calendar;

import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.calendar.value.EventUserView;
import com.psk.backend.calendar.value.EventView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.user.User;
import com.psk.backend.user.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.annotation.Resource;

@Mapper(config = BaseMapperConfig.class)
public abstract class EventMapper {

    @Resource
    private UserRepository userRepository;

    abstract Event create(EventForm form);

    @Mapping(target = "trip", expression = "java(false)")
    @Mapping(target = "owner", expression = "java(e.getCreatedBy().getUserId().equals(userId))")
    @Mapping(target = "userStatus", expression = "java(e.getUsers().stream()" +
            ".filter(u -> u.getId().equals(userId)).findFirst().get().getStatus())")
    abstract EventListView listView(Event e, String userId);

    abstract Event update(EventForm form, @MappingTarget Event event);

    public EventUser user(String id) {
        return userRepository
                .findById(id).map(this::user)
                .getOrElse(null);
    }

    @Mapping(target = "status", expression = "java(EventUserStatus.CONFIRMATION_PENDING)")
    abstract EventUser user(User user);

    abstract EventView view(Event event);

    abstract EventUserView userView(EventUser user);
}
