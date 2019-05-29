package com.psk.backend.trip.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidMergeableTripsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidMergeableTrips {
    String message() default "{com.psk.backend.trip.validation" +
            ".ValidMergeableTripsValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}