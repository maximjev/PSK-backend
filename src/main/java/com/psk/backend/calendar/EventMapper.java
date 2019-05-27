package com.psk.backend.calendar;

import com.psk.backend.calendar.value.EventForm;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.calendar.value.EventUserForm;
import com.psk.backend.calendar.value.EventView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripUserForm;
import com.psk.backend.user.User;
import com.psk.backend.user.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Mapper(config = BaseMapperConfig.class)
public abstract class EventMapper {

    @Resource
    private UserRepository userRepository;

    abstract Event create(EventForm form);

    abstract EventListView listView(Event event);

    abstract EventView view(Event event);

    abstract Event update(EventForm form, @MappingTarget Event event);

    public EventUser user(EventUserForm form) {
        return userRepository
                .findById(form.getUserId()).map(u ->
                        this.user(u).isAtEvent(form.isAtEvent()))
                .getOrElse(null);
    }
    public List<EventUserForm> fromTripForm(List<TripUserForm> form){
        return form.stream()
                .map(u -> new EventUserForm(u.getUserId(), true))
                .collect(toList());
    }

    public LocalDateTime end(TripForm form){
        return form.isReservation()
                ? form.getReservationBegin()
                : form.getArrival();
    }

    @Mapping(source = "departure", target = "start")
    @Mapping(expression = "java(end(form))", target = "end")
    @Mapping(expression = "java(fromTripForm(form.getUsers()))", target = "users")
    public abstract EventForm fromTrip(TripForm form);


    @Mapping(target = "status", expression = "java(EventUserStatus.CONFIRMATION_PENDING)")
    abstract EventUser user(User user);
}
