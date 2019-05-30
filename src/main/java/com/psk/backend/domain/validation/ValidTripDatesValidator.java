package com.psk.backend.domain.validation;

import com.psk.backend.domain.trip.value.TripForm;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

@Component
public class ValidTripDatesValidator implements ConstraintValidator<ValidTripDates, TripForm> {


    @Override
    public void initialize(ValidTripDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(TripForm value, ConstraintValidatorContext context) {
        if (value.isReservation()) {
            return value.getDeparture().isAfter(LocalDateTime.now())
                    && value.getDeparture().isBefore(value.getReservationBegin())
                    && value.getReservationBegin().isBefore(value.getReservationEnd());
        } else {
            return value.getDeparture().isAfter(LocalDateTime.now())
                    && value.getArrival().isAfter(value.getDeparture());
        }
    }
}