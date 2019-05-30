package com.psk.backend.domain.validation;

import com.psk.backend.repository.ApartmentRepository;
import com.psk.backend.domain.trip.value.TripCreateForm;
import io.atlassian.fugue.extensions.step.Steps;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ValidTripApartmentsValidator implements ConstraintValidator<ValidTripApartments, TripCreateForm> {

    @Resource
    private ApartmentRepository repository;

    @Override
    public void initialize(ValidTripApartments constraintAnnotation) {
    }

    @Override
    public boolean isValid(TripCreateForm value, ConstraintValidatorContext context) {
        return Steps.begin(repository.findById(value.getSource()))
                .then(a1 -> repository.findById(value.getDestination()))
                .yield((a1, a2) -> !a1.getId().equals(a2.getId()))
                .getOrElse(() -> false);
    }
}