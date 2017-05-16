package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.AddEditBaseActivity;
import stoyanov.valentin.mycar.activities.abstracts.NewBaseActivity;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateTimePickerUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.RealmUtils;
import stoyanov.valentin.mycar.utils.TextUtils;

public class NewRefuelingActivity extends AddEditBaseActivity {

    private TextInputLayout tilQuantity;
    private Spinner spnFuelTanks;
    private Switch switchIsFull;
    private RealmResults<FuelTank> results;
    private String fuelTankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_refueling);
        initComponents();
        setComponentListeners();
        setContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setEnabled(false);
        int id = item.getItemId();
        if (id == R.id.action_save) {
            progressBar.setIndeterminate(true);
            if (isInputValid()) {
                fuelTankId = results.get(spnFuelTanks.getSelectedItemPosition()).getId();
                saveToRealm();
            }else {
                item.setEnabled(true);
                progressBar.setIndeterminate(false);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initComponents() {
        super.initComponents();
        tilQuantity = (TextInputLayout) findViewById(R.id.til_new_refueling_quantity);
        switchIsFull = (Switch) findViewById(R.id.switch_new_refueling_is_full);
        spnFuelTanks = (Spinner) findViewById(R.id.spn_new_refueling_fuel_tanks);
        results = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, vehicleId)
                .findFirst()
                .getFuelTanks()
                .where()
                .findAll();
        ArrayList<String> fuelTypeNames = getFuelTypeNamesFromResults();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, fuelTypeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFuelTanks.setAdapter(adapter);
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        switchIsFull.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FuelTank fuelTank = getFuelTankFromSpinner();
                    TextUtils.setTextToTil(tilQuantity, String.valueOf(fuelTank.getCapacity()));
                    tilQuantity.setEnabled(false);
                }else {
                    TextUtils.setTextToTil(tilQuantity, "");
                    tilQuantity.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;

        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilQuantity))) {
            valid = false;
            tilQuantity.setError("Quantity should be number");
        }else {
            if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilQuantity)) >
                    getFuelTankFromSpinner().getCapacity()) {
                valid = false;
                tilQuantity.setError("Quantity is more than capacity");
            }else {
                if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilQuantity)) < NumberUtils.INTEGER_ZERO) {
                    valid = false;
                    tilQuantity.setError("Quantity should not be negative");
                }
            }
        }

        return super.isInputValid() && valid;
    }

    @Override
    protected void populateNewItem() {
        Calendar calendar = Calendar.getInstance();
        btnDate.setText(DateUtils.dateToString(calendar.getTime()));
        btnTime.setText(DateUtils.timeToString(calendar.getTime()));    }

    @Override
    protected void populateExistingItem() {
        Refueling refueling = myRealm.where(Refueling.class)
                .equalTo(RealmTable.ID, itemId)
                .findFirst();
        FuelTank fuelTank = refueling.getFuelTank();
        spnFuelTanks.setSelection(results.indexOf(fuelTank));
        btnDate.setText(DateUtils.dateToString(refueling.getDate()));
        btnTime.setText(DateUtils.timeToString(refueling.getDate()));
        TextUtils.setTextToTil(tilOdometer, String.valueOf(refueling.getOdometer()));
        if (refueling.getQuantity() == fuelTank.getCapacity()) {
            switchIsFull.setChecked(true);
        } else {
            switchIsFull.setChecked(false);
            TextUtils.setTextToTil(tilQuantity, String.valueOf(refueling.getQuantity()));
        }
        TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(refueling.getPrice())));
        TextUtils.setTextToTil(tilNote, refueling.getNote());
    }

    @Override
    protected void saveItem(Realm realm) {
        Vehicle vehicle = realm.where(Vehicle.class)
                .equalTo(RealmTable.ID, vehicleId)
                .findFirst();
        Refueling refueling = new Refueling();

        if (isNewItem()) {
            refueling.setId(UUID.randomUUID().toString());
        }else {
            vehicle.getRefuelings()
                    .where()
                    .equalTo(RealmTable.ID, itemId)
                    .findFirst()
                    .deleteFromRealm();
            refueling.setId(itemId);
        }

        FuelTank fuelTank = realm.where(FuelTank.class)
                .equalTo(RealmTable.ID, fuelTankId)
                .findFirst();
        refueling.setFuelTank(fuelTank);

        String price = TextUtils.getTextFromTil(tilPrice);
        refueling.setPrice(MoneyUtils.stringToLong(price));

        String quantity = TextUtils.getTextFromTil(tilQuantity);
        refueling.setQuantity(NumberUtils.createInteger(quantity));
        refueling.setFuelPrice(MoneyUtils.calculateFuelPrice(price, quantity));

        Date date = DateUtils.stringToDate(btnDate.getText().toString());
        Date time = DateUtils.stringToTime(btnTime.getText().toString());
        refueling.setDate(DateUtils.dateTime(date, time));

        long odometer = Long.parseLong(TextUtils.getTextFromTil(tilOdometer));
        refueling.setOdometer(odometer);
        if (odometer > getVehicleOdometer()) {
            vehicle.setOdometer(odometer);
            setVehicleOdometer(odometer);
        }
        refueling.setNote(TextUtils.getTextFromTil(tilNote));
        vehicle.getRefuelings().add(realm.copyToRealmOrUpdate(refueling));
    }

    @Override
    protected void onItemSaved() {
        if (isNewItem()) {
            showMessage("New refueling saved!");
        }else {
            showMessage("Refueling updated!");
            Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
            intent.putExtra(RealmTable.ID, vehicleId);
            intent.putExtra(RealmTable.REFUELINGS + RealmTable.ID, itemId);
            intent.putExtra(RealmTable.TYPE, ActivityType.REFUELING.ordinal());
            startActivity(intent);
        }

        finish();
    }

    private ArrayList<String> getFuelTypeNamesFromResults() {
        ArrayList<String> types = new ArrayList<>(results.size());
        for (FuelTank fuelTank : results) {
            types.add(fuelTank.getType());
        }
        return types;
    }

    private FuelTank getFuelTankFromSpinner() {
        int position = spnFuelTanks.getSelectedItemPosition();
        return results.get(position);
    }

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setEnabled(false);
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (isInputValid()) {
                saveToRealm();
            }else {
                item.setEnabled(true);
            }
            return true;
        }else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initComponents() {
        super.initComponents();
        refuelingId = getIntent().getStringExtra(RealmTable.REFUELINGS + RealmTable.ID);
        if (refuelingId != null) {
            setUpdate(true);
        }
        tilTime = (TextInputLayout) findViewById(R.id.til_new_refueling_time);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_refueling_price);
        tilQuantity = (TextInputLayout) findViewById(R.id.til_new_refueling_quantity);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_btn_new_refueling_full_ft);
        spnFuelTanks = (Spinner) findViewById(R.id.spn_new_refueling_fuel_tanks);
        results = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, getVehicleId())
                .findFirst().getFuelTanks().where().findAll();
        ArrayList<String> fuelTypeNames = getFuelTypeNamesFromResults();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, fuelTypeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFuelTanks.setAdapter(adapter);
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        DateTimePickerUtils.addTimePickerListener(NewRefuelingActivity.this, tilTime);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    FuelTank fuelTank = getFuelTankFromSpinner();
                    TextUtils.setTextToTil(tilQuantity, String.valueOf(fuelTank.getCapacity()));
                    tilQuantity.setEnabled(false);
                }else {
                    TextUtils.setTextToTil(tilQuantity, "");
                    tilQuantity.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void setContent() {
        tilDate.setHint("Date");
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_refueling_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
        if (isUpdate()) {
            Refueling refueling = myRealm.where(Refueling.class)
                    .equalTo(RealmTable.ID, refuelingId)
                    .findFirst();
            FuelTank fuelTank = refueling.getFuelTank();
            spnFuelTanks.setSelection(results.indexOf(fuelTank));
            TextUtils.setTextToTil(tilDate, DateUtils.dateToString(refueling.getDate()));
            TextUtils.setTextToTil(tilTime, DateUtils.timeToString(refueling.getDate()));
            TextUtils.setTextToTil(tilOdometer, String.valueOf(refueling.getOdometer()));
            if (refueling.getQuantity() == fuelTank.getCapacity()) {
                toggleButton.setChecked(true);
            } else {
                toggleButton.setChecked(false);
                TextUtils.setTextToTil(tilQuantity, String.valueOf(refueling.getQuantity()));
            }
            TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(refueling.getPrice())));
            TextUtils.setTextToTil(tilNote, refueling.getNote());
        }else {
            Calendar calendar = Calendar.getInstance();
            TextUtils.setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        }
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilPrice))) {
            valid = false;
            tilPrice.setError("Price should be number");
        }
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilQuantity))) {
            valid = false;
            tilQuantity.setError("Quantity should be number");
        }else {
            if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilQuantity)) >
                    getFuelTankFromSpinner().getCapacity()) {
                valid = false;
                tilQuantity.setError("Quantity is more than capacity");
            }else {
                if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilQuantity)) < NumberUtils.INTEGER_ZERO) {
                    valid = false;
                    tilQuantity.setError("Quantity should not be negative");
                }
            }
        }
        return super.isInputValid() && valid;
    }

    @Override
    public void saveToRealm() {
        final String fuelTankId = results.get(spnFuelTanks.getSelectedItemPosition()).getId();
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, getVehicleId())
                        .findFirst();
                Refueling refueling = new Refueling();

                if (isUpdate()) {
                    vehicle.getRefuelings()
                            .where()
                            .equalTo(RealmTable.ID, refuelingId)
                            .findFirst()
                            .deleteFromRealm();
                    refueling.setId(refuelingId);
                }else {
                    refueling.setId(UUID.randomUUID().toString());
                }

                FuelTank fuelTank = realm.where(FuelTank.class)
                        .equalTo(RealmTable.ID, fuelTankId)
                        .findFirst();
                refueling.setFuelTank(fuelTank);

                String price = TextUtils.getTextFromTil(tilPrice);
                refueling.setPrice(MoneyUtils.stringToLong(price));

                String quantity = TextUtils.getTextFromTil(tilQuantity);
                refueling.setQuantity(NumberUtils.createInteger(quantity));
                refueling.setFuelPrice(MoneyUtils.calculateFuelPrice(price, quantity));

                Date date = DateUtils.stringToDate(TextUtils.getTextFromTil(tilDate));
                Date time = DateUtils.stringToTime(TextUtils.getTextFromTil(tilTime));
                refueling.setDate(DateUtils.dateTime(date, time));

                long odometer = Long.parseLong(TextUtils.getTextFromTil(tilOdometer));
                refueling.setOdometer(odometer);
                if (odometer > getVehicleOdometer()) {
                    vehicle.setOdometer(odometer);
                    setVehicleOdometer(odometer);
                }
                refueling.setNote(TextUtils.getTextFromTil(tilNote));
                vehicle.getRefuelings().add(realm.copyToRealmOrUpdate(refueling));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isUpdate()) {
                    showMessage("Refueling updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(RealmTable.ID, getVehicleId());
                    intent.putExtra(RealmTable.REFUELINGS + RealmTable.ID, refuelingId);
                    intent.putExtra(RealmTable.TYPE, ActivityType.REFUELING.ordinal());
                    startActivity(intent);
                }else {
                    showMessage("New refueling saved!");
                }
                odometerChanged(getVehicleOdometer());
                finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                showMessage("Something went wrong...");
                error.printStackTrace();
                finish();
            }
        });
    }

    private ArrayList<String> getFuelTypeNamesFromResults() {
        ArrayList<String> types = new ArrayList<>(results.size());
        for (FuelTank fuelTank : results) {
            types.add(fuelTank.getType());
        }
        return types;
    }

    private FuelTank getFuelTankFromSpinner() {
        int position = spnFuelTanks.getSelectedItemPosition();
        return results.get(position);
    }*/
}
