package account.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MonthYearValidator implements ConstraintValidator<ValidMonthYear, String> {

    @Override
    public void initialize(ValidMonthYear constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy");
        sdf.setLenient(false); // Disable lenient mode to disallow invalid dates

        try {
            sdf.parse(value);
            return true; // If parsing succeeds, the date is valid
        } catch (ParseException e) {
            return false; // Parsing failed, the date is invalid
        }
    }
}
