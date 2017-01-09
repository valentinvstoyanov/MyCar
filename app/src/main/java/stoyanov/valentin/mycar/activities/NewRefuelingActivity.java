package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.transition.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.FuelType;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class NewRefuelingActivity extends BaseActivity{

    private Realm myRealm;
    private TextInputLayout tilDate, tilTime, tilNotes, tilOdometer, tilQuantity, tilPrice;
    private Spinner spnFuelTanks;
    private ToggleButton toggleButton;
    private String vehicleId;
    private long vehicleOdometer;
    private RealmResults<FuelTank> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_refueling);
        initComponents();
        setComponentListeners();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myRealm != null) {
            myRealm.close();
        }
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
                final String fuelTankId = results.get(spnFuelTanks.getSelectedItemPosition()).getId();
                myRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Refueling refueling = realm.createObject(Refueling.class,
                                UUID.randomUUID().toString());
                        Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                        note.setContent(tilNotes.getEditText().getText().toString());
                        refueling.setNote(note);
                        Action action = realm.createObject(Action.class,
                                UUID.randomUUID().toString());
                        try {
                            Calendar calendar = Calendar.getInstance();
                            Date date = DateUtils.stringToDate(tilDate.getEditText()
                                    .getText().toString());
                            calendar.setTime(date);
                            Date time = DateUtils.stringToTime(tilTime.getEditText()
                                    .getText().toString());
                            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                            calendar.set(Calendar.MINUTE, time.getMinutes());
                            action.setDate(calendar.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long odometer = Long.parseLong(tilOdometer.getEditText()
                                .getText().toString());
                        action.setOdometer(odometer);
                        String price = tilPrice.getEditText().getText().toString();
                        action.setPrice(MoneyUtils.stringToLong(price));
                        refueling.setAction(action);
                        String quantity = tilQuantity.getEditText().getText().toString();
                        Log.d("Quantity: ", quantity);
                        refueling.setFuelPrice(MoneyUtils.calculateFuelPrice(price, quantity));
                        Log.d("FuelPrice : ", String.valueOf(refueling.getFuelPrice()));
                        refueling.setQuantity(Integer.parseInt(quantity));
                        refueling.setFuelTankId(fuelTankId);
                        Vehicle vehicle = realm.where(Vehicle.class)
                                .equalTo(RealmTable.ID, vehicleId)
                                .findFirst();
                        vehicle.getRefuelings().add(refueling);
                        vehicle.setOdometer(odometer);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        showMessage("New refueling saved!");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        showMessage("Something went wrong...");
                        error.printStackTrace();
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
        if (Integer.parseInt(tilQuantity.getEditText().getText().toString()) >
                getFuelTankFromSpinnerValue().getCapacity()) {
            valid = false;
            tilQuantity.setError("Quantity is more than capacity");
        }
        return valid;
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tilDate = (TextInputLayout) findViewById(R.id.til_new_refueling_date);
        tilTime = (TextInputLayout) findViewById(R.id.til_new_refueling_time);
        Calendar calendar = Calendar.getInstance();
        tilDate.getEditText().setText(DateUtils.dateToString(calendar.getTime()));
        tilTime.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
        tilOdometer = (TextInputLayout) findViewById(R.id.til_new_refueling_odometer);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_refueling_price);
        tilNotes = (TextInputLayout) findViewById(R.id.til_new_refueling_notes);
        tilQuantity = (TextInputLayout) findViewById(R.id.til_new_refueling_quantity);
        spnFuelTanks = (Spinner) findViewById(R.id.spn_new_refueling_fuel_tanks);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_btn_new_refueling_full_ft);
        myRealm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
        vehicleOdometer = intent.getLongExtra(RealmTable.ODOMETER, 0);
        TextView tvLastOdometer = (TextView) findViewById(R.id.tv_new_refueling_last_odometer);
        String text = String.format(getString(R.string.last_odometer_placeholder), vehicleOdometer);
        tvLastOdometer.setText(text);
        myRealm = Realm.getDefaultInstance();
        results = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, vehicleId)
                .findFirst().getFuelTanks().where().findAll();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner,
                getFuelTankTypesFromResults());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFuelTanks.setAdapter(adapter);
    }

    @Override
    protected void setComponentListeners() {
        tilDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewRefuelingActivity.this,
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewRefuelingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                tilTime.getEditText()
                                        .setText(DateUtils.timeToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    FuelTank fuelTank = getFuelTankFromSpinnerValue();
                    if (fuelTank != null) {
                        tilQuantity.getEditText().setText(String.valueOf(fuelTank.getCapacity()));
                    }
                    tilQuantity.setEnabled(false);
                }else{
                    tilQuantity.setEnabled(true);
                    tilQuantity.getEditText().setText("");
                }
            }
        });
    }

    private ArrayList<String> getFuelTankTypesFromResults() {
        ArrayList<String> types = new ArrayList<>(results.size());
        for (FuelTank fuelTank : results) {
            types.add(fuelTank.getFuelType().getName());
        }
        return types;
    }

    private FuelTank getFuelTankFromSpinnerValue() {
        for (FuelTank fuelTank : results) {
            if (fuelTank.getFuelType().getName()
                    .equals(spnFuelTanks.getSelectedItem().toString())) {
                return fuelTank;
            }
        }
        return null;
    }
}
