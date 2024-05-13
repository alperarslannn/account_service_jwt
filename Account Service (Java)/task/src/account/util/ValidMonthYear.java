package account.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MonthYearValidator.class)
public @interface ValidMonthYear {
    String message() default "Invalid month-year format (MM-yyyy)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}