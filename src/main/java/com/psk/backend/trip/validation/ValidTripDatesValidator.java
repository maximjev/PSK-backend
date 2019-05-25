package com.psk.backend.trip.validation;

import com.psk.backend.trip.value.TripForm;
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
        if(value.isNoReservation()) {
            return value.getDepartion().isAfter(LocalDateTime.now())
                    && value.getArrival().isBefore(value.getDepartion());
        } else {
            return value.getDepartion().isAfter(LocalDateTime.now())
                    && value.getDepartion().isBefore(value.getReservationBegin())
                    && value.getReservationBegin().isBefore(value.getReservationEnd());
        }
    }
}