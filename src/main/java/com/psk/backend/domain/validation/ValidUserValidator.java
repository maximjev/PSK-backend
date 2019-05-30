package com.psk.backend.domain.validation;

import com.psk.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Component
public class ValidUserValidator implements ConstraintValidator<ValidUser, String> {

    @Resource
    private UserRepository repository;

    @Override
    public void initialize(ValidUser constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return repository.findById(value).isSuccess();
    }
}
