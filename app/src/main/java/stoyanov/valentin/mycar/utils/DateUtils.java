package stoyanov.valentin.mycar.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String manufacturePattern = "MMM yyyy";
    private static final String pattern = "dd.MM.yyyy";
    private static final String timePattern = "hh:mm";
    private static final String datePattern = pattern + " " + timePattern;

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

    public static String timeToString(Date time) {
        DateFormat dateFormat = new SimpleDateFormat(timePattern, Locale.getDefault());
        return dateFormat.format(time);
    }

    public static Date stringToTime(String time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(timePattern, Locale.getDefault());
        return dateFormat.parse(time);
    }

    public static String datetimeToString(Date datetime) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
        return dateFormat.format(datetime);
    }

    public static Date stringToDatetime(String date, String time) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
        return dateFormat.parse(date + " " + time);
    }
}