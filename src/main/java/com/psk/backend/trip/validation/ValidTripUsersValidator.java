package com.psk.backend.trip.validation;

import com.psk.backend.trip.TripUser;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ValidTripUsersValidator implements ConstraintValidator<ValidTripUsers, List<TripUser>> {

    @Override
    public void initialize(ValidTripUsers constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<TripUser> value, ConstraintValidatorContext context) {
        boolean allHaveHotel = value.stream()
                .filter(tripUser -> !tripUser.isInApartment())
                .allMatch(a -> a.getResidenceAddress() != null);

        var ids = value.stream().map(TripUser::getId).collect(Collectors.toList());

        return ids.stream().noneMatch(i -> Collections.frequency(ids, i) > 1) && allHaveHotel;
    }
}