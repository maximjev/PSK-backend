package com.psk.backend.trip;

import com.psk.backend.apartment.Address;
import com.psk.backend.apartment.ApartmentRepository;
import com.psk.backend.common.address.AddressView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.trip.value.*;
import com.psk.backend.user.User;
import com.psk.backend.user.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.annotation.Resource;

@Mapper(config = BaseMapperConfig.class)
public abstract class TripMapper {

    @Resource
    private UserRepository userRepository;

    @Resource
    private ApartmentRepository apartmentRepository;

    @Mapping(target = "status", expression = "java(TripStatus.DRAFT)")
    abstract Trip create(TripCreateForm form);

    @Mapping(source = "source.address", target = "sourceAddress")
    @Mapping(source = "destination.address", target = "destinationAddress")
    abstract TripListView listView(Trip trip);

    abstract TripView view(Trip trip);

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
}
