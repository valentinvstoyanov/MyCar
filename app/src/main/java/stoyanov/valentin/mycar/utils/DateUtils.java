package stoyanov.valentin.mycar.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String manufacturePattern = "MMM yyyy";
    private static final String pattern = "dd.MM.yyyy";

    public static String manufactureDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(manufacturePattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date manufactureStringToDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(manufacturePattern, Locale.getDefault());
        return dateFormat.parse(date);
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date stringToDate(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.parse(date);
    }
}