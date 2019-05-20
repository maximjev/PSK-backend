package com.psk.backend.trip.validation;

import com.psk.backend.user.UserRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;


@Component
public class ValidUsersValidator implements ConstraintValidator<ValidUsers, List<String>> {

    @Resource
    private UserRepository repository;

    @Override
    public void initialize(ValidUsers constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        return value.stream().allMatch(u -> repository.findById(u).isSuccess());
    }
}
