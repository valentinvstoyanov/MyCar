package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
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
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.NewBaseActivity;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class NewRefuelingActivity extends NewBaseActivity {

    private TextInputLayout tilTime, tilQuantity, tilPrice;
    private Spinner spnFuelTanks;
    private ToggleButton toggleButton;
    private RealmResults<FuelTank> results;
    private String refuelingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_refueling);
        initComponents();
        setComponentListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (isInputValid()) {
                saveToRealm();
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
        tilDate.setHint("Date");
        tilTime = (TextInputLayout) findViewById(R.id.til_new_refueling_time);
        Calendar calendar = Calendar.getInstance();
        setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_refueling_price);
        tilQuantity = (TextInputLayout) findViewById(R.id.til_new_refueling_quantity);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_btn_new_refueling_full_ft);
        spnFuelTanks = (Spinner) findViewById(R.id.spn_new_refueling_fuel_tanks);
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_refueling_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
        refuelingId = getIntent().getStringExtra(RealmTable.REFUELINGS + RealmTable.ID);
        if (refuelingId != null) {
            setUpdate(true);
        }
        Log.d(getVehicleId(), "initComponents: ");
        results = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, getVehicleId())
                .findFirst().getFuelTanks().where().findAll();
        ArrayList<String> fuelTypeNames = getFuelTypeNamesFromResults();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, fuelTypeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFuelTanks.setAdapter(adapter);
        if (isUpdate()) {
            setContent();
        }
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        addTimePickerListener(tilTime);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    FuelTank fuelTank = getFuelTankFromSpinner();
                    setTextToTil(tilQuantity, String.valueOf(fuelTank.getCapacity()));
                    tilQuantity.setEnabled(false);
                    Log.d(getTextFromTil(tilQuantity), "onCheckedChanged: ");
                }else {
                    setTextToTil(tilQuantity, "");
                    tilQuantity.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void setContent() {
        Refueling refueling = myRealm.where(Refueling.class)
                .equalTo(RealmTable.ID, refuelingId)
                .findFirst();
        FuelTank fuelTank = myRealm.where(FuelTank.class)
                .equalTo(RealmTable.ID, refueling.getFuelTankId())
                .findFirst();
        spnFuelTanks.setSelection(results.indexOf(fuelTank));
        setTextToTil(tilDate, DateUtils.dateToString(refueling.getAction().getDate()));
        setTextToTil(tilTime, DateUtils.timeToString(refueling.getAction().getDate()));
        if (refueling.getQuantity() == fuelTank.getCapacity()) {
            toggleButton.setChecked(true);
        }else{
            toggleButton.setChecked(false);
        }
        setTextToTil(tilQuantity, String.valueOf(tilQuantity));
        setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(refueling.getAction().getPrice())));
    }

    @Override
    public boolean isInputValid() {
        boolean result = super.isInputValid();
        boolean valid = true;
        if (!NumberUtils.isCreatable(getTextFromTil(tilPrice))) {
            valid = false;
            tilPrice.setError("Price should be number");
        }
        if (!NumberUtils.isCreatable(getTextFromTil(tilQuantity))) {
            valid = false;
            tilQuantity.setError("Quantity should be number");
        }else {
            if (NumberUtils.createInteger(getTextFromTil(tilQuantity)) >
                    getFuelTankFromSpinner().getCapacity()) {
                valid = false;
                tilQuantity.setError("Quantity is more than capacity");
            }else {
                if (NumberUtils.createInteger(getTextFromTil(tilQuantity)) < NumberUtils.INTEGER_ZERO) {
                    valid = false;
                    tilQuantity.setError("Quantity should not be negative");
                }
            }
        }
        return result && valid;
    }

    @Override
    public void saveToRealm() {
        final String fuelTankId = results.get(spnFuelTanks.getSelectedItemPosition()).getId();
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Refueling refueling;
                if (isUpdate()) {
                    refueling = realm.where(Refueling.class)
                            .equalTo(RealmTable.ID, refuelingId)
                            .findFirst();
                    refueling.getAction().deleteFromRealm();
                    refueling.getNote().deleteFromRealm();
                } else {
                    refueling = realm.createObject(Refueling.class, UUID.randomUUID().toString());
                }

                Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                note.setContent(getTextFromTil(tilNote));
                refueling.setNote(note);

                Action action = realm.createObject(Action.class,
                        UUID.randomUUID().toString());
                Calendar calendar = Calendar.getInstance();
                Date date = DateUtils.stringToDate(getTextFromTil(tilDate));
                calendar.setTime(date);
                Date time = DateUtils.stringToTime(getTextFromTil(tilTime));
                calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                calendar.set(Calendar.MINUTE, time.getMinutes());
                action.setDate(calendar.getTime());
                long odometer = Long.parseLong(getTextFromTil(tilOdometer));
                action.setOdometer(odometer);
                String price = getTextFromTil(tilPrice);
                action.setPrice(MoneyUtils.stringToLong(price));
                refueling.setAction(action);

                String quantity = getTextFromTil(tilQuantity);
                refueling.setFuelPrice(MoneyUtils.calculateFuelPrice(price, quantity));
                refueling.setQuantity(Integer.parseInt(quantity));
                refueling.setFuelTankId(fuelTankId);

                if (!isUpdate()) {
                    Vehicle vehicle = realm.where(Vehicle.class)
                            .equalTo(RealmTable.ID, getVehicleId())
                            .findFirst();
                    vehicle.getRefuelings().add(refueling);
                    vehicle.setOdometer(odometer);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isUpdate()) {
                    showMessage("Refueling updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(RealmTable.ID, getVehicleId());
                    intent.putExtra(RealmTable.REFUELINGS + RealmTable.ID, refuelingId);
                    intent.putExtra(RealmTable.TYPE, ViewActivity.ViewType.REFUELING.ordinal());
                    startActivity(intent);
                }else {
                    showMessage("New refueling saved!");
                }
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
            types.add(fuelTank.getFuelType().getName());
        }
        return types;
    }

    private FuelTank getFuelTankFromSpinner() {
        int position = spnFuelTanks.getSelectedItemPosition();
        return results.get(position);
    }
}
