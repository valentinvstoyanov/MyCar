package stoyanov.valentin.mycar.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String manufacturePattern = "MMM yyyy";
    private static final String pattern = "dd.MM.yyyy";
    private static final String timePattern = "HH:mm";
    private static final String datePattern = pattern + " " + timePattern;

    public static String manufactureDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(manufacturePattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date manufactureStringToDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat(manufacturePattern, Locale.getDefault());
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date stringToDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String timeToString(Date time) {
        DateFormat dateFormat = new SimpleDateFormat(timePattern, Locale.getDefault());
        return dateFormat.format(time);
    }

    public static Date stringToTime(String time) {
        DateFormat dateFormat = new SimpleDateFormat(timePattern, Locale.getDefault());
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String datetimeToString(Date datetime) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
        return dateFormat.format(datetime);
    }

    public static Date stringToDatetime(String date, String time) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern, Locale.getDefault());
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static boolean isDateInFuture(String strDate, String strTime) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 2);
        Date today = now.getTime();
        Date date = stringToDatetime(strDate, strTime);
        return today.compareTo(date) < 0;
    }

    public static boolean isDateInPast(Date date) {
        Date today = new Date();
        return today.compareTo(date) > 0 ;
    }
}