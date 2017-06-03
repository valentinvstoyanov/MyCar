package stoyanov.valentin.mycar.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String MANUFACTURE_PATTERN = "MMM yyyy";
    private static final String PATTERN = "dd.MM.yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String DATE_PATTERN = PATTERN + " " + TIME_PATTERN;

    public static String manufactureDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(MANUFACTURE_PATTERN, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date manufactureStringToDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat(MANUFACTURE_PATTERN, Locale.getDefault());
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat(PATTERN, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date stringToDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat(PATTERN, Locale.getDefault());
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String timeToString(Date time) {
        DateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        return dateFormat.format(time);
    }

    public static Date stringToTime(String time) {
        DateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        Date parsedDate = new Date();
        try {
            parsedDate = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String datetimeToString(Date datetime) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        return dateFormat.format(datetime);
    }

    public static Date stringToDatetime(String date, String time) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
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
        Date today = now.getTime();
        Date date = stringToDatetime(strDate, strTime);
        return today.compareTo(date) < 0;
    }

    public static boolean isDateInFuture(String strDate) {
        Calendar now = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.setTime(stringToDate(strDate));

        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);

        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.HOUR_OF_DAY, 0);

        return now.compareTo(date) < 0;
    }

    public static boolean isDateInPast(String strDate, String strTime) {
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();
        Date date = stringToDatetime(strDate, strTime);
        return today.compareTo(date) > 0;
    }

    public static boolean isNotValidDate(String date, boolean time) {
        SimpleDateFormat dateFormat;
        if (time) {
            dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        }else {
            dateFormat = new SimpleDateFormat(PATTERN, Locale.getDefault());
        }
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    public static String getDateFromInts(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return DateUtils.dateToString(calendar.getTime());
    }

    public static String getTimeFromInts(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return DateUtils.timeToString(calendar.getTime());
    }

    public static Date dateTime(Date date, Date time) {
        Calendar cDate = Calendar.getInstance();
        Calendar cTime = Calendar.getInstance();
        cDate.setTime(date);
        cTime.setTime(time);
        cDate.set(Calendar.HOUR_OF_DAY, cTime.get(Calendar.HOUR_OF_DAY));
        cDate.set(Calendar.MINUTE, cTime.get(Calendar.MINUTE));
        cDate.set(Calendar.SECOND, 0);
        cDate.set(Calendar.MILLISECOND, 0);

        return cDate.getTime();
    }

    public static boolean isExpirationDateValid(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Calendar parsedDate = Calendar.getInstance();
        parsedDate.setTime(stringToDate(date));

        return parsedDate.compareTo(calendar) >= 0;
    }
}