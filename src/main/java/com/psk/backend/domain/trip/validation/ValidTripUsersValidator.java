package com.psk.backend.domain.trip.validation;

import com.psk.backend.domain.trip.value.TripForm;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ValidTripUsersValidator implements ConstraintValidator<ValidTripUsers, TripForm> {

    @Override
    public void initialize(ValidTripUsers constraintAnnotation) {
    }

    @Override
    public boolean isValid(TripForm value, ConstraintValidatorContext context) {
        boolean allHaveHotel = value.getUsers().stream()
                .filter(tripUser -> !tripUser.isInApartment())
                .allMatch(a -> a.getResidenceAddress() != null);

        var ids = value.getUsers().stream().map(u -> u.getUserId()).collect(Collectors.toList());

        return ids.stream().noneMatch(i -> Collections.frequency(ids, i) > 1) && allHaveHotel;
    }
}