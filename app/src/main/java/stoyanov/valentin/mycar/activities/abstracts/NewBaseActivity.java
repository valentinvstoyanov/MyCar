package stoyanov.valentin.mycar.activities.abstracts;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.activities.interfaces.INewBaseActivity;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;

public abstract class NewBaseActivity extends BaseActivity
                    implements INewBaseActivity{

    private boolean update = false;
    private String vehicleId;
    private long vehicleOdometer;
    protected OnOdometerChangeListener listener;
    protected Realm myRealm;
    protected TextInputLayout tilDate, tilOdometer, tilNote;

/*
    abstract protected void saveToRealm();*/
    /*abstract protected void setContent();
    abstract protected void saveToRealm();*/

    @Override
    public void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tilDate = (TextInputLayout) findViewById(R.id.til_date);
        Calendar calendar = Calendar.getInstance();
        setTextToTil(tilDate, DateUtils.dateToString(calendar.getTime()));
        tilOdometer = (TextInputLayout) findViewById(R.id.til_odometer);
        tilNote = (TextInputLayout) findViewById(R.id.til_note);
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
        vehicleOdometer = intent.getLongExtra(RealmTable.ODOMETER, 0);
        myRealm = Realm.getDefaultInstance();
        listener = new OnOdometerChangeListener() {
            @Override
            public void onChange(long odometer) {
                RealmResults<Service> services = myRealm.where(Service.class)
                        .lessThanOrEqualTo(RealmTable.TARGET_ODOMETER, 800)
                        .findAll();
                for (Service service : services) {
                    //if (odometer + 200 > service.getTargetOdometer()) {
                        Notification notification = NotificationUtils.createNotification(getApplicationContext(),
                                vehicleId, RealmTable.SERVICES + RealmTable.ID, service.getId(),
                                ViewActivity.ViewType.SERVICE, ViewActivity.class, "Service",
                                service.getType().getName() + " should be revised at " + service.getTargetOdometer(),
                                R.drawable.ic_services_black);
                    NotificationManager manager = (NotificationManager)
                            getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(0, notification);
                   // }
                }
            }
        };
    }

    @Override
    public void setComponentListeners() {
        addDatePickerListener(tilDate);
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    protected String getVehicleId() {
        return vehicleId;
    }

    protected void setCurrentOdometer(TextView textView) {
        String text = String.format(getString(R.string.current_odometer_placeholder),
                vehicleOdometer);
        textView.setText(String.valueOf(text));
    }

    protected void setTextToTil(TextInputLayout til, String text) {
        TextInputEditText tiEt = (TextInputEditText) til.getEditText();
        if (tiEt != null) {
            tiEt.setText(text);
        }
    }

    protected void setTextToAutoComplete(TextInputLayout til, String text) {
        AutoCompleteTextView acTv = (AutoCompleteTextView) til.getEditText();
        if (acTv != null) {
            acTv.setText(text);
        }
    }

    protected String getTextFromAutoComplete(TextInputLayout til) {
        AutoCompleteTextView acTv = (AutoCompleteTextView) til.getEditText();
        return acTv != null ? acTv.getText().toString() : "";
    }

    protected String getTextFromTil(TextInputLayout til) {
        TextInputEditText tiEt = (TextInputEditText) til.getEditText();
        return tiEt != null ? tiEt.getText().toString() : "";
    }

    protected void addDatePickerListener(final TextInputLayout til) {
        TextInputEditText tiet = (TextInputEditText) til.getEditText();
        if (tiet != null) {
            tiet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar calendar = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            calendar.set(year, month, day);

                        }
                    };
                    DatePickerDialog datePickerDialog = new DatePickerDialog
                            (NewBaseActivity.this, dateListener,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                            );
                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    datePicker.setMaxDate(calendar.getTime().getTime());
                    datePickerDialog.show();
                }
            });
        }
    }

    protected void addTimePickerListener(final TextInputLayout til) {
        TextInputEditText tiet = (TextInputEditText) til.getEditText();
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
                            til.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(NewBaseActivity.this,
                            timeListener, calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }
            });
        }
    }

    public long getVehicleOdometer() {
        return vehicleOdometer;
    }

    public void setVehicleOdometer(long vehicleOdometer) {
        this.vehicleOdometer = vehicleOdometer;
    }

    public boolean isInputValid() {
        boolean valid = true;
        if (!NumberUtils.isCreatable(getTextFromTil(tilOdometer))) {
            valid = false;
            tilOdometer.setError("Numeric value expected");
        }
        if (NumberUtils.createLong(getTextFromTil(tilOdometer)) < NumberUtils.LONG_ZERO) {
            valid = false;
            tilOdometer.setError("Odometer can't be negative");
        }
        /*if (ValidationUtils.isInputValid(getTextFromTil(tilNote))
                || !TextUtils.isEmpty(getTextFromTil(tilNote))) {
            valid = false;
            tilNote.setError("Invalid characters");
        }*/
        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }

    protected interface OnOdometerChangeListener{
        void onChange(long odometer);
    }
}
