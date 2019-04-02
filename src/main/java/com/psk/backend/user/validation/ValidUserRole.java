package com.psk.backend.user.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUserRoleValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidUserRole {
    String message() default "{com.psk.backend.user.validation" +
            ".ValidUserRole.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
