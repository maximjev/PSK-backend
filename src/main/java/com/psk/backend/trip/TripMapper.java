package com.psk.backend.trip;

import com.psk.backend.apartment.Address;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.common.address.AddressView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.trip.value.*;
import com.psk.backend.user.User;
import org.mapstruct.*;


@Mapper(config = BaseMapperConfig.class)
@DecoratedWith(TripMapperDecorator.class)
interface TripMapper {

    @Mapping(target = "status", expression = "java(TripStatus.DRAFT)")
    Trip create(TripCreateForm form);



    @Mapping(source = "source.address", target = "sourceAddress")
    @Mapping(source = "destination.address", target = "destinationAddress")
    TripListView listView(Trip trip);

    @Mapping(source = "source.address", target = "source")
    @Mapping(source = "destination.address", target = "destination")
    TripView view(Trip trip);

    @Mapping(source = "id", target = "userId")
    TripUserForm tripUserForm(TripUser trip);

    Trip update(TripForm form, @MappingTarget Trip trip);

    TripUser user(TripUserForm form);

    TripApartment apartment(String id);

    @Mapping(target = "status", expression = "java(TripUserStatus.CONFIRMATION_PENDING)")
    TripUser user(User user);

    TripUser tripUser(TripUserForm form, @MappingTarget TripUser user);

    AddressView address(Address address);

    @Mapping(target = "tripId", source = "id")
    @Mapping(expression = "java(com.psk.backend.common.address.AddressFormatter.formatAddress(trip.getSource().getAddress()))",
            target = "sourceAddress")
    @Mapping(expression = "java(com.psk.backend.common.address.AddressFormatter.formatAddress(trip.getDestination().getAddress()))",
            target = "residenceAddress")
    @Mapping(ignore = true, target = "carRent")
    @Mapping(ignore = true, target = "flightTicket")
    TripUserView tripUserView(Trip trip);

    @Mapping(source = "departure", target = "start")
    @Mapping(expression = "java(trip.isReservation()" +
            " ? trip.getReservationBegin()" +
            " : trip.getArrival())", target = "end")
    @Mapping(target = "trip", expression = "java(true)")
    EventListView toEvent(Trip trip);
}
