package com.psk.backend.user.validation;


import com.psk.backend.user.UserRole;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
public class ValidUserRoleValidator implements ConstraintValidator<ValidUserRole, String> {

    @Override
    public void initialize(ValidUserRole constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return List
                .of(UserRole.values())
                .stream()
                .anyMatch(r -> value.equals(r.toString()));
    }
}
