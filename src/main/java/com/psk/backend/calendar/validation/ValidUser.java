package com.psk.backend.calendars.validation;

import javax.validation.Payload;
import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUserValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidUser {
    String message() default "{com.psk.backend.user.validation" +
            ".ValidUsersValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

