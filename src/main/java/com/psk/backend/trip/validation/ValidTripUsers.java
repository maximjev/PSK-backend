package com.psk.backend.trip.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidTripUsersValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidTripUsers {
    String message() default "{com.psk.backend.trip.validation" +
            ".ValidTripUsers.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}