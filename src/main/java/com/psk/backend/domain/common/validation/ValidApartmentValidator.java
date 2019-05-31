package com.psk.backend.domain.common.validation;

import com.psk.backend.repository.ApartmentRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Component
public class ValidApartmentValidator implements ConstraintValidator<ValidApartment, String> {

    @Resource
    private ApartmentRepository repository;

    @Override
    public void initialize(ValidApartment constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return repository.findById(value).isSuccess();
    }
}