package com.psk.backend.trip;

import com.psk.backend.appartment.Address;
import com.psk.backend.appartment.AppartmentRepository;
import com.psk.backend.common.address.AddressView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripListView;
import com.psk.backend.trip.value.TripUserForm;
import com.psk.backend.trip.value.TripView;
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
    private AppartmentRepository appartmentRepository;

    @Mapping(target = "status", expression = "java(TripStatus.DRAFT)")
    abstract Trip create(TripForm form);

    @Mapping(source = "source", target = "sourceAddress")
    @Mapping(source = "destination", target = "destinationAddress")
    abstract TripListView listView(Trip trip);

    abstract TripView view(Trip trip);

    abstract Trip update(TripForm form, @MappingTarget Trip trip);

    public TripUser user(TripUserForm form) {
        return userRepository
                .findById(form.getUserId()).map(u ->
                        this.user(u).isInAppartment(form.isInAppartment()))
                .getOrElse(null);
    }

    @Mapping(target = "status", expression = "java(TripUserStatus.CONFIRMATION_PENDING)")
    abstract TripUser user(User user);

    public AddressView addressView(String appartmentId) {
        return appartmentRepository.findById(appartmentId)
                .map(a -> this.address(a.getAddress()))
                .getOrElse(null);
    }

    public abstract AddressView address(Address address);
}
