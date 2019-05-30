package com.psk.backend.domain.validation;

import com.psk.backend.domain.calendar.value.EventForm;
import com.psk.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Component
public class ValidEventFormValidator implements ConstraintValidator<ValidEventForm, EventForm> {


    @Resource
    private UserRepository userRepository;

    @Override
    public void initialize(ValidEventForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventForm value, ConstraintValidatorContext context) {
        boolean validDates = value.getStart().isAfter(LocalDateTime.now())
                && value.getEnd().isAfter(value.getStart());

        boolean validUsers = value.getUsers().stream().allMatch(u -> userRepository.findById(u).isSuccess());

        return validDates && validUsers;
    }
}