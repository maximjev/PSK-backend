package com.psk.backend.trip.validation;

import com.psk.backend.appartment.AppartmentRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Component
public class ValidAppartmentValidator implements ConstraintValidator<ValidAppartment, String> {

    @Resource
    private AppartmentRepository repository;

    @Override
    public void initialize(ValidAppartment constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return repository.findById(value).isSuccess();
    }
}