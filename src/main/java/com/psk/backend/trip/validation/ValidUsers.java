package com.psk.backend.trip.validation;


import com.psk.backend.user.validation.ValidUserRoleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUserRoleValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidUsers {
    String message() default "{com.psk.backend.user.validation" +
            ".ValidUsersValidator.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

