package com.psk.backend.domain.common.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidTripValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidTrip {
    String message() default "{com.psk.backend.domain.common.validation" +
            ".ValidTripValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

