package com.psk.backend.trip;

import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripListView;
import com.psk.backend.trip.value.TripView;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapperConfig.class)
public interface TripMapper {
    Trip create(TripForm form);

    TripListView listView(Trip trip);

    TripView view(Trip trip);

    Trip update(TripForm form, @MappingTarget Trip trip);
}
