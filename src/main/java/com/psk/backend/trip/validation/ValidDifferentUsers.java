package com.psk.backend.trip.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDifferentUsersValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidDifferentUsers {
    String message() default "{com.psk.backend.trip.validation" +
            ".ValidDifferentUsers.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}