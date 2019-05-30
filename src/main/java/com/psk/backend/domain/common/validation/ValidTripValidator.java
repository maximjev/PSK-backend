package com.psk.backend.domain.common.validation;

import com.psk.backend.domain.trip.TripRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Component
public class ValidTripValidator implements ConstraintValidator<ValidTrip, String> {

    @Resource
    private TripRepository repository;

    @Override
    public void initialize(ValidTrip constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return repository.findById(value).isSuccess();
    }
}
