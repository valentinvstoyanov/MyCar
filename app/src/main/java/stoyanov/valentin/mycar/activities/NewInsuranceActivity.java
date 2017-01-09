package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class NewInsuranceActivity extends BaseActivity {

    private TextInputLayout tilDate, tilTime, tilExpirationDate;
    private TextInputLayout tilExpirationTime, tilOdometer, tilPrice, tilNotes;
    private Realm myRealm;
    private String vehicleId;
    private long vehicleOdometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_insurance);
        initComponents();
        setComponentListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if(isInputValid()) {
                myRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Insurance insurance = realm.createObject(Insurance.class,
                                UUID.randomUUID().toString());
                        Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                        note.setContent(tilNotes.getEditText().getText().toString());
                        insurance.setNote(note);
                        Action action = realm.createObject(Action.class, UUID.randomUUID().toString());
                        try {
                            Calendar calendar = Calendar.getInstance();
                            Date date = DateUtils.stringToDate(tilDate.getEditText().getText().toString());
                            calendar.setTime(date);
                            Date time = DateUtils.stringToTime(tilTime.getEditText().getText().toString());
                            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                            calendar.set(Calendar.MINUTE, time.getMinutes());
                            action.setDate(calendar.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long odometer = Long.parseLong(tilOdometer.getEditText()
                                .getText().toString());
                        action.setOdometer(odometer);
                        long price = MoneyUtils.stringToLong(tilOdometer.getEditText()
                                .getText().toString());
                        action.setPrice(price);
                        insurance.setAction(action);
                        try {
                            insurance.setExpirationDate(DateUtils.stringToDatetime(tilExpirationDate.getEditText()
                                            .getText().toString(),
                                    tilExpirationTime.getEditText().getText().toString()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Vehicle vehicle = realm.where(Vehicle.class)
                                .equalTo(RealmTable.ID, vehicleId)
                                .findFirst();
                        vehicle.getInsurances().add(insurance);
                        vehicle.setOdometer(odometer);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Notification notification = newNotification("Insurance",
                                "You should renew your insurance", R.drawable.ic_insurance_black);
                        try {
                            Date notificationDate = DateUtils.stringToDatetime(
                                    tilDate.getEditText().getText().toString(),
                                    tilTime.getEditText().getText().toString());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(notificationDate);
                            addNotification(notification, calendar.getTimeInMillis());
                            Log.d("onSuccess1: ", String.valueOf(calendar.getTimeInMillis()));
                            Log.d("onSuccess2: ", String.valueOf(SystemClock.elapsedRealtime() + 120000));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        showMessage("New insurance saved!");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        showMessage("Something went wrong...");
                    }
                });
                finish();
            }
            return true;
        }else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isInputValid() {
        boolean valid = true;
        if (Long.parseLong(tilOdometer.getEditText().getText().toString()) < vehicleOdometer) {
            valid = false;
            tilOdometer.setError("The value is smaller than expected");
        }
        if (tilPrice.getEditText().getText().toString().length() < 1) {
            valid = false;
            tilPrice.setError("No price entered");
        }
        return valid;
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tilDate = (TextInputLayout) findViewById(R.id.til_new_insurance_date);
        tilTime = (TextInputLayout) findViewById(R.id.til_new_insurance_time);
        Calendar calendar = Calendar.getInstance();
        tilDate.getEditText().setText(DateUtils.dateToString(calendar.getTime()));
        tilTime.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
        tilExpirationDate = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_date);
        tilExpirationTime = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_time);
        calendar.add(Calendar.MINUTE, 2);
        tilExpirationDate.getEditText().setText(DateUtils.dateToString(calendar.getTime()));
        tilExpirationTime.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
        tilOdometer = (TextInputLayout) findViewById(R.id.til_new_insurance_odometer);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_insurance_price);
        tilNotes = (TextInputLayout) findViewById(R.id.til_new_insurance_notes);
        TextView tvLastOdometer = (TextView) findViewById(R.id.tv_new_insurance_last_odometer);
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
        vehicleOdometer = intent.getLongExtra(RealmTable.ODOMETER, 0);
        tvLastOdometer.setText(String.valueOf(vehicleOdometer));
        myRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void setComponentListeners() {
        tilDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewInsuranceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                tilDate.getEditText()
                                        .setText(DateUtils.dateToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(calendar.getTime().getTime());
                datePickerDialog.show();
            }
        });
        tilTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewInsuranceActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                tilTime.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
        tilExpirationDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewInsuranceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                tilExpirationDate.getEditText()
                                        .setText(DateUtils.dateToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(calendar.getTime().getTime());
                datePickerDialog.show();
            }
        });
        tilExpirationTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewInsuranceActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                tilExpirationTime.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }
}
