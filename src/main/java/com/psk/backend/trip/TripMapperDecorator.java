package com.psk.backend.trip;

import com.psk.backend.apartment.ApartmentRepository;
import com.psk.backend.trip.value.TripCreateForm;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.trip.value.TripUserForm;
import com.psk.backend.user.UserRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

import static com.psk.backend.common.address.AddressFormatter.formatAddress;


public abstract class TripMapperDecorator implements TripMapper{


    @Resource
    private UserRepository userRepository;

    @Resource
    private ApartmentRepository apartmentRepository;



    @Override
    public TripUser user(TripUserForm form) {
        return userRepository
                .findById(form.getUserId()).map(u ->
                        this.tripUser(form, this.user(u)))
                .getOrElse(TripUser::new);
    }

    @Override
    public TripApartment apartment(String id) {
        return apartmentRepository.findById(id)
                .map(a -> new TripApartment(id, a.getAddress()))
                .getOrElse(() -> new TripApartment(id));
    }

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
}
