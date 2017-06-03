package stoyanov.valentin.mycar.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class DateTimePickerUtils {

    public static void showDatePicker(Context context, DatePickerDialog.OnDateSetListener listener) {
        Calendar dateTime = Calendar.getInstance();
        int day = dateTime.get(Calendar.DAY_OF_MONTH);
        int month = dateTime.get(Calendar.MONTH);
        int year = dateTime.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener, year, month, day);
        datePickerDialog.show();
    }

    public static void showTimePicker(Context context, TimePickerDialog.OnTimeSetListener listener) {
        Calendar dateTime = Calendar.getInstance();
        int hour = dateTime.get(Calendar.HOUR_OF_DAY);
        int minute = dateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, listener, hour, minute, true);
        timePickerDialog.show();
    }
}
