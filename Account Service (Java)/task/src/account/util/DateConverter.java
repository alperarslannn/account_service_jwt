package account.util;

import account.exception.InvalidPeriodException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Converter
public class DateConverter implements AttributeConverter<Date, String> {

    private final DateFormat dateFormat = new SimpleDateFormat("MM-yyyy");

    @Override
    public String convertToDatabaseColumn(Date date) {
        if (date == null) {
            return null;
        }
        return dateFormat.format(date);
    }

    @Override
    public Date convertToEntityAttribute(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new InvalidPeriodException();
        }
    }
}
