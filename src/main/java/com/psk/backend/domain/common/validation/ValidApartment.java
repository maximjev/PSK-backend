package com.psk.backend.domain.common.validation;



import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidApartmentValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidApartment {
    String message() default "{com.psk.backend.domain.user.validation" +
            ".ValidApartment.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

