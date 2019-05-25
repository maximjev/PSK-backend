package com.psk.backend.common.validation;



import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidAppartmentValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidAppartment {
    String message() default "{com.psk.backend.user.validation" +
            ".ValidAppartment.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

