package com.psk.backend.common.validation;



import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidApartmentValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidApartment {
    String message() default "{com.psk.backend.common.validation" +
            ".ValidApartment.message}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

