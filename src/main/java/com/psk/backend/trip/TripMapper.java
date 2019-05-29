package com.psk.backend.trip;

import com.psk.backend.apartment.Address;
import com.psk.backend.apartment.ApartmentRepository;
import com.psk.backend.calendar.value.EventListView;
import com.psk.backend.common.address.AddressView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.trip.value.*;
import com.psk.backend.user.User;
import com.psk.backend.user.UserRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.annotation.Resource;

import static com.psk.backend.common.address.AddressFormatter.formatAddress;

@Mapper(config = BaseMapperConfig.class)
public abstract class TripMapper {

    @Resource
    private UserRepository userRepository;

    @Resource
    private ApartmentRepository apartmentRepository;

    @Mapping(target = "status", expression = "java(TripStatus.DRAFT)")
    abstract Trip create(TripCreateForm form);

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
    abstract TripListView listView(Trip trip);

    @Mapping(source = "source.address", target = "source")
    @Mapping(source = "destination.address", target = "destination")
    @Mapping(source = "destination.id", target = "destinationId")
    abstract TripView view(Trip trip);

    @Mapping(source = "id", target = "userId")
    abstract TripUserForm tripUserForm(TripUser trip);

    abstract Trip update(TripForm form, @MappingTarget Trip trip);

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

    @Mapping(target = "status", expression = "java(TripUserStatus.CONFIRMATION_PENDING)")
    abstract TripUser user(User user);

    abstract TripUser tripUser(TripUserForm form, @MappingTarget TripUser user);

    public abstract AddressView address(Address address);

    @Mapping(target = "tripId", source = "id")
    @Mapping(expression = "java(com.psk.backend.common.address.AddressFormatter.formatAddress(trip.getSource().getAddress()))",
            target = "sourceAddress")
    @Mapping(expression = "java(com.psk.backend.common.address.AddressFormatter.formatAddress(trip.getDestination().getAddress()))",
            target = "residenceAddress")
    @Mapping(ignore = true, target = "carRent")
    @Mapping(ignore = true, target = "flightTicket")
    public abstract TripUserView tripUserView(Trip trip);

    @Mapping(source = "departure", target = "start")
    @Mapping(expression = "java(trip.isReservation()" +
            " ? trip.getReservationBegin()" +
            " : trip.getArrival())", target = "end")
    @Mapping(target = "trip", expression = "java(true)")
    public abstract EventListView toEvent(Trip trip);
}
