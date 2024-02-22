package com.banana.AccountsService.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {OpeningDate.Validator.class})
public @interface OpeningDate {
    String message() default "{openingDate.afterNow}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public class Validator implements ConstraintValidator<OpeningDate, Date> {
        @Override
        public void initialize(final OpeningDate annotation) {
        }

        @Override
        public boolean isValid(final Date openingDate, final ConstraintValidatorContext context) {
            if (openingDate == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{openingDate.not.null}").addConstraintViolation();
                return false;
            }
            Date currentDate = new Date();
            return openingDate.before(currentDate);
        }
    }
}


