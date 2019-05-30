package com.psk.backend.domain.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidTripValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidTrip {
    String message() default "{com.psk.backend.domain.validation" +
            ".ValidTripValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

