package com.psk.backend.mapper;

import com.psk.backend.domain.apartment.Address;
import com.psk.backend.repository.ApartmentRepository;
import com.psk.backend.domain.calendar.value.EventListView;
import com.psk.backend.domain.common.address.AddressView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.domain.trip.Trip;
import com.psk.backend.domain.trip.TripApartment;
import com.psk.backend.domain.trip.TripUser;
import com.psk.backend.domain.trip.value.*;
import com.psk.backend.domain.user.User;
import com.psk.backend.repository.UserRepository;
import org.mapstruct.*;

import javax.annotation.Resource;

import static com.psk.backend.domain.common.address.AddressFormatter.formatAddress;

@Mapper(config = BaseMapperConfig.class)
@DecoratedWith(TripMapperDecorator.class)
public abstract class TripMapper {

    @Resource
    private UserRepository userRepository;

    @Resource
    private ApartmentRepository apartmentRepository;

    @Mapping(target = "status", expression = "java(com.psk.backend.domain.trip.TripStatus.DRAFT)")
    public abstract Trip create(TripCreateForm form);

    @AfterMapping
    public void afterMapping(TripCreateForm form, @MappingTarget Trip trip) {
        trip.getUsers().stream()
                .filter(TripUser::isInApartment)
                .forEach(u -> u.setResidenceAddress(formatAddress(trip.getDestination().getAddress())));
    }

    @AfterMapping
    public void afterMapping(TripForm form, @MappingTarget Trip trip) {
        trip.getUsers().stream()
                .filter(TripUser::isInApartment)
                .forEach(u -> u.setResidenceAddress(formatAddress(trip.getDestination().getAddress())));
    }

    @Mapping(source = "source.address", target = "sourceAddress")
    @Mapping(source = "destination.address", target = "destinationAddress")
    public abstract TripListView listView(Trip trip);

    @Mapping(source = "source.address", target = "source")
    @Mapping(source = "destination.address", target = "destination")
    @Mapping(source = "destination.id", target = "destinationId")
    public abstract TripView view(Trip trip);

    @Mapping(source = "id", target = "userId")
    public abstract TripUserForm tripUserForm(TripUser trip);

    public abstract Trip update(TripForm form, @MappingTarget Trip trip);

    public TripUser user(TripUserForm form) {
        return userRepository
                .findById(form.getUserId()).map(u ->
                        this.tripUser(form, this.user(u)))
                .getOrElse(TripUser::new);
    }

    public TripApartment apartment(String id) {
        return apartmentRepository.findById(id)
                .map(a -> new TripApartment(id, a.getAddress()))
                .getOrElse(() -> new TripApartment(id));
    }

    @Mapping(target = "status", expression = "java(com.psk.backend.domain.trip.TripUserStatus.CONFIRMATION_PENDING)")
    public abstract TripUser user(User user);

    public abstract TripUser tripUser(TripUserForm form, @MappingTarget TripUser user);

    public abstract AddressView address(Address address);

    @Mapping(target = "tripId", source = "trip.id")
    @Mapping(expression = "java(com.psk.backend.domain.common.address.AddressFormatter.formatAddress(trip.getSource().getAddress()))",
            target = "sourceAddress")
    @Mapping(expression = "java(com.psk.backend.domain.common.address.AddressFormatter.formatAddress(trip.getDestination().getAddress()))",
            target = "residenceAddress")
    @Mapping(ignore = true, target = "carRent")
    @Mapping(ignore = true, target = "flightTicket")
    public abstract TripUserView tripUserView(Trip trip, String userId);

    @Mapping(source = "departure", target = "start")
    @Mapping(expression = "java(trip.isReservation()" +
            " ? trip.getReservationBegin()" +
            " : trip.getArrival())", target = "end")
    @Mapping(target = "trip", expression = "java(true)")
    public abstract EventListView toEvent(Trip trip);
}
