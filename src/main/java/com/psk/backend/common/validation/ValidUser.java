package com.psk.backend.common.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUserValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidUser {
    String message() default "{com.psk.backend.user.validation" +
            ".ValidUserValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

