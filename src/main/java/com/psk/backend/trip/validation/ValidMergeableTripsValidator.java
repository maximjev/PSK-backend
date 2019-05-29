package com.psk.backend.trip.validation;

import com.psk.backend.trip.value.TripMergeForm;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ValidMergeableTripsValidator implements ConstraintValidator<ValidMergeableTrips, TripMergeForm> {

    @Override
    public void initialize(ValidMergeableTrips constraintAnnotation) {
    }

    @Override
    public boolean isValid(TripMergeForm value, ConstraintValidatorContext context) {
        return !value.getTripOne().equals(value.getTripTwo());
    }
}