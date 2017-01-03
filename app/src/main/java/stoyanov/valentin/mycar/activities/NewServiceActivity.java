package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.ServiceType;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;

public class NewServiceActivity extends BaseActivity {

    private TextInputLayout tilDate, tilTime, tilOdometer, tilPrice, tilNotes, tilType;
    private Vehicle vehicle;
    private Realm myRealm;
    private RealmResults<ServiceType> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
        initComponents();
        setComponentListeners();
        Intent intent = getIntent();
        String vehicleId = intent.getStringExtra(ViewVehicleActivity.VEHICLE_ID);
        vehicle = myRealm.where(Vehicle.class).equalTo(RealmTable.ID, vehicleId).findFirstAsync();
        AutoCompleteTextView actvType = (AutoCompleteTextView) findViewById(R.id.actv_new_service_type);
        results = myRealm.where(ServiceType.class).findAll();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                getStringsFromServiceTypes());
        actvType.setAdapter(adapter);
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
            if (isInputValid()) {
                myRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Service service = realm.createObject(Service.class, UUID.randomUUID().toString());
                        String serviceTypeValue = tilType.getEditText().getText().toString();
                        ServiceType type = realm.where(ServiceType.class)
                                .equalTo(RealmTable.NAME, serviceTypeValue).findFirst();
                        if (type == null) {
                            type = realm.createObject(ServiceType.class, UUID.randomUUID().toString());
                            type.setName(serviceTypeValue);
                        }
                        service.setType(type);
                        Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                        note.setContent(tilNotes.getEditText().getText().toString());
                        service.setNote(note);
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
                        action.setOdometer(Long.parseLong(
                                tilOdometer.getEditText().getText().toString()));
                        BigDecimal bigDecimal = new BigDecimal(tilOdometer.getEditText()
                                .getText().toString());
                        action.setPrice(bigDecimal.longValue());
                        service.setAction(action);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        showMessage("New service saved!");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        showMessage("Something went wrong...");
                    }
                });
            }
            finish();
            return true;
        }else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }

    private ArrayList<String> getStringsFromServiceTypes() {
        ArrayList<String> arrayList = new ArrayList<>(results.size());
        for (ServiceType type : results) {
            arrayList.add(type.getName());
        }
        return arrayList;
    }

    private boolean isInputValid() {
        boolean valid = true;
        if (Long.parseLong(tilOdometer.getEditText().getText().toString()) < vehicle.getOdometer()) {
            valid = false;
            tilOdometer.setError("The value is smaller than expected");
        }
        if (tilPrice.getEditText().toString().length() < 1) {
            valid = false;
            tilOdometer.setError("No price entered");
        }
        if (tilType.getEditText().getText().toString().length() < 1) {
            valid = false;
            showMessage("No service type entered");
        }
        return valid;
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tilDate = (TextInputLayout) findViewById(R.id.til_new_service_date);
        tilTime = (TextInputLayout) findViewById(R.id.til_new_service_time);
        tilOdometer = (TextInputLayout) findViewById(R.id.til_new_service_odometer);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_service_price);
        tilNotes = (TextInputLayout) findViewById(R.id.til_new_service_notes);
        tilType = (TextInputLayout) findViewById(R.id.til_new_service_type);
        myRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void setComponentListeners() {
        tilDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewServiceActivity.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewServiceActivity.this,
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
    }
}
