package com.psk.backend.trip.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidTripApartmentsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidTripApartments {
    String message() default "{com.psk.backend.trip.validation" +
            ".ValidTripApartmentsValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}