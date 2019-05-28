package com.psk.backend.calendar.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidEventDatesValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ValidEventDates {
    String message() default "{com.psk.backend.calendar.validation" +
            ".ValidEventDatesValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}