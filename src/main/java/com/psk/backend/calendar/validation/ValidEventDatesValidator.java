package com.psk.backend.calendar.validation;

import com.psk.backend.calendar.value.EventForm;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Component
public class ValidEventDatesValidator implements ConstraintValidator<ValidEventDates, EventForm> {


    @Override
    public void initialize(ValidEventDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventForm value, ConstraintValidatorContext context) {
        return value.getStart().isAfter(LocalDateTime.now())
                && value.getEnd().isAfter(value.getStart());
    }
}