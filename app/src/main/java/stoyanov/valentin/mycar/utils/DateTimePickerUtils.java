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
        final TextInputEditText tiet = (TextInputEditText) til.getEditText();
        if (tiet != null) {
            tiet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            calendar.set(year, month, day);
                            tiet.setText(DateUtils.dateToString(calendar.getTime()));
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
        final TextInputEditText tiet = (TextInputEditText) til.getEditText();
        if (tiet != null) {
            tiet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar = Calendar.getInstance();
                    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            tiet.setText(DateUtils.timeToString(calendar.getTime()));
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
}
