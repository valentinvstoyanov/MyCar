package stoyanov.valentin.mycar.activities;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;
import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;
import io.realm.Realm;
import io.realm.RealmList;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IVehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleRepository;

public class NewVehicleActivity extends BaseActivity{
    private Spinner spnVehicleType;
    private EditText etName;
    private AutoCompleteTextView actvBrand;
    private AutoCompleteTextView actvModel;
    private EditText etOdometer;
    private EditText etHorsePower;
    private EditText etCubicCentimeters;
    private EditText etManufactureDate;
    private View vColor;
    private EditText etRegistrationPlate;
    private EditText etVinPlate;
    private EditText etNote;

    private Spinner spnMainFuelTankType;
    private EditText etMainFTCapacity;
    private EditText etMainFTConsumption;

    private ToggleButton toggleButton;

    private LinearLayout llSecondFuelTank;
    private Spinner spnSecondFuelTankType;
    private EditText etSecondFTCapacity;
    private EditText etSecondFTConsumption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        initComponents();
        setComponentListeners();

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
            saveToRealm(getVehicle());
            finish();
            return true;
        }else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_vehicle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        spnVehicleType = (Spinner) findViewById(R.id.spn_new_vehicle_type);
        etName = (EditText) findViewById(R.id.et_new_vehicle_name);
        actvBrand = (AutoCompleteTextView) findViewById(R.id.actv_new_vehicle_brand);
        actvModel = (AutoCompleteTextView) findViewById(R.id.actv_new_vehicle_model);
        etOdometer = (EditText) findViewById(R.id.et_new_vehicle_odometer);
        etHorsePower = (EditText) findViewById(R.id.et_new_vehicle_hp);
        etCubicCentimeters = (EditText) findViewById(R.id.et_new_vehicle_cc);
        etManufactureDate = (EditText) findViewById(R.id.et_new_vehicle_manifacture_date);
        vColor = findViewById(R.id.view_new_vehicle_color);
        etRegistrationPlate = (EditText) findViewById(R.id.et_new_vehicle_registration_plate);
        etVinPlate = (EditText) findViewById(R.id.et_new_vehicle_vin_plate);
        etNote = (EditText) findViewById(R.id.et_new_vehicle_notes);
        toggleButton = (ToggleButton) findViewById(R.id.tbtn_additional_fuel_tank);
        vColor = findViewById(R.id.view_new_vehicle_color);
        spnMainFuelTankType = (Spinner) findViewById(R.id.spn_new_vehicle_fuel_tank_type);
        etMainFTCapacity = (EditText) findViewById(R.id.et_new_vehicle_fuel_tank_capacity);
        etMainFTConsumption = (EditText) findViewById(R.id.et_new_vehicle_fuel_tank_consumption);
    }

    @Override
    protected void setComponentListeners() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    llSecondFuelTank = (LinearLayout) findViewById(R.id.ll_new_vehicle_second_fuel_tank);
                    llSecondFuelTank.setVisibility(View.VISIBLE);
                    spnSecondFuelTankType = (Spinner) findViewById(R.id.spn_new_vehicle_second_fuel_tank_type);
                    etSecondFTCapacity = (EditText) findViewById(R.id.et_new_vehicle_second_fuel_tank_capacity);
                    etSecondFTConsumption = (EditText) findViewById(R.id.et_new_vehicle_second_fuel_tank_consumption);
                } else {
                    llSecondFuelTank.setVisibility(View.GONE);
                    llSecondFuelTank = null;
                    etSecondFTCapacity = null;
                    etSecondFTConsumption = null;
                }
            }
        });

        vColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorOMaticDialog.Builder()
                        .initialColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                        .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS
                        .indicatorMode(IndicatorMode.HEX) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                        .onColorSelected(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(@ColorInt int i) {
                                vColor.setBackgroundColor(i);
                            }
                        })
                        //.showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                        .create()
                        .show(getSupportFragmentManager(), "choose_vehicle_color");
            }
        });
    }

    private Vehicle getVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setName("MoqtaK0la");
        vehicle.setColor(vColor.getSolidColor());
        return vehicle;
    }

    private void saveToRealm(Vehicle vehicle) {
        VehicleRepository repository = new VehicleRepository();
        repository.addVehicle(vehicle, new IVehicleRepository.OnWritesCallback() {
            @Override
            public void onSuccess(String message) {
                showMessage(message);
            }

            @Override
            public void onError(Exception e) {
                showMessage("Something went wrong...");
                e.printStackTrace();
            }
        });
    }
}
