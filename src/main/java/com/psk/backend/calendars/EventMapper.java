package com.psk.backend.user;

import com.psk.backend.config.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import com.psk.backend.calendars.value.EventForm;
import com.psk.backend.calendars.value.EventListView;
import com.psk.backend.calendars.value.EventView;

@Mapper(config = BaseMapperConfig.class)
public interface EventMapper {

    Event create(EventForm form);

    EventListView listView(Event event);

    EventView view(Event event);

    Event update(EventForm form, @MappingTarget Event event);
}