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

    public static void addDatePickerListener(final Context context, TextInputLayout til,
                                             final Date date, final PickerLimits limits) {
        final TextInputEditText textInputEditText = (TextInputEditText) til.getEditText();
        if (textInputEditText != null) {
            textInputEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            calendar.set(year, month, day);
                            textInputEditText.setText(DateUtils.dateToString(calendar.getTime()));
                        }
                    };
                    DatePickerDialog datePickerDialog = new DatePickerDialog
                            (context, dateListener,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            );
                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    switch (limits) {
                        case MIN:
                            datePicker.setMinDate(date.getTime());
                            break;
                        case MAX:
                            datePicker.setMaxDate(date.getTime());
                            break;
                    }
                    datePickerDialog.show();
                }
            });
        }
    }

    public static void addTimePickerListener(final Context context, TextInputLayout til) {
        final TextInputEditText textInputEditText = (TextInputEditText) til.getEditText();
        if (textInputEditText != null) {
            textInputEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar = Calendar.getInstance();
                    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            textInputEditText.setText(DateUtils.timeToString(calendar.getTime()));
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, timeListener,
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }
            });
        }
    }

    public enum PickerLimits {
        MIN, MAX
    }

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
