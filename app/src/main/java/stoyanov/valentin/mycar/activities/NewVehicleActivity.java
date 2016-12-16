package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.adapters.NewFuelTankRecyclerViewAdapter;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IBrandRepository;
import stoyanov.valentin.mycar.realm.repositories.IVehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.BrandRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleRepository;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.ValidationUtils;

public class NewVehicleActivity extends BaseActivity{

    private static final String MY_CAR_PREFERENCES = "myCarPreferences";
    private static final String IS_FIRST_LAUNCH = "isFirstLaunch";

    private Spinner spnVehicleType;
    private TextInputLayout tilName, tilBrand, tilModel, tilOdometer, tilHorsePower,
            tilCubicCentimeters, tilRegistrationPlate, tilVinPlate, tilNotes, tilManufactureDate;
    private EditText etColor;
    private View viewColor;
    private Button btnAddFuelTank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        initComponents();
        setComponentListeners();
        /*BrandRepository brandRepository = new BrandRepository();
        brandRepository.getAllBrands(new IBrandRepository.OnGetAllBrandsCallback() {
            @Override
            public void onSuccess(RealmResults<Brand> results) {
                String[] brandNames = BrandRepository.getBrandNames(results.toArray(new Brand[0]));
                ArrayAdapter<String> actvBrandsAdapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, brandNames);
            }
        });*/
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
                saveToRealm(getVehicle());
                finish();
            }else {
                showMessage("Incorrect input");
            }
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
        tilName = (TextInputLayout) findViewById(R.id.til_new_vehicle_name);
        tilBrand = (TextInputLayout) findViewById(R.id.til_new_vehicle_brand);
        tilModel = (TextInputLayout) findViewById(R.id.til_new_vehicle_model);
        tilOdometer = (TextInputLayout) findViewById(R.id.til_new_vehicle_odometer);
        tilHorsePower = (TextInputLayout) findViewById(R.id.til_new_vehicle_horse_power);
        tilCubicCentimeters = (TextInputLayout) findViewById(R.id.til_new_vehicle_cubic_centimeters);
        tilRegistrationPlate = (TextInputLayout) findViewById(R.id.til_new_vehicle_registration_plate);
        tilVinPlate = (TextInputLayout) findViewById(R.id.til_new_vehicle_vin_plate);
        tilNotes = (TextInputLayout) findViewById(R.id.til_new_vehicle_notes);
        tilManufactureDate = (TextInputLayout) findViewById(R.id.til_new_vehicle_manufacture_date);
        etColor = (EditText) findViewById(R.id.et_vehicle_color);
        viewColor = findViewById(R.id.view_new_vehicle_color);
        etColor.setText(String.valueOf(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null)));
        btnAddFuelTank = (Button) findViewById(R.id.btn_new_vehicle_add_ft);
     }

    @Override
    protected void setComponentListeners() {
        tilManufactureDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewVehicleActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                tilManufactureDate.getEditText()
                                        .setText(DateUtils.manufactureDateToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(calendar.getTime().getTime());
                datePickerDialog.show();
            }
        });

    }

    public void colorPicker(View view) {
        new ColorOMaticDialog.Builder()
                .initialColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null))
                .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS
                .indicatorMode(IndicatorMode.HEX) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                .onColorSelected(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(@ColorInt int i) {
                        etColor.setText(String.valueOf(i));
                        viewColor.setBackgroundColor(i);
                        btnAddFuelTank.setBackgroundColor(i);
                    }
                })
                //.showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                .create()
                .show(getSupportFragmentManager(), "choose_vehicle_color");
    }

    private boolean isInputValid() {
        boolean valid = true;
        if (!ValidationUtils.isInputValid(tilName.getEditText().getText().toString())) {
            tilName.setError("Incorrect vehicle name");
            valid = false;
        }
        if (!ValidationUtils.isInputValid(tilBrand.getEditText().getText().toString())) {
            tilBrand.setError("Incorrect vehicle brand");
            valid = false;
        }
        if (!ValidationUtils.isInputValid(tilModel.getEditText().getText().toString())) {
            tilModel.setError("Incorrect vehicle model");
            valid = false;
        }
        if(tilRegistrationPlate.getEditText().getText().toString().length() < 1) {
            tilRegistrationPlate.setError("Incorrect registration plate");
            valid = false;
        }
        if (tilVinPlate.getEditText().getText().toString().length() < 1) {
            tilVinPlate.setError("Incorrect VIN number");
            valid = false;
        }
        if (tilOdometer.getEditText().getText().toString().length() < 1) {
            tilOdometer.setError("No odometer value provided");
            valid = false;
        }
        if (tilManufactureDate.getEditText().getText().toString().length() < 4) {
            tilManufactureDate.setError("Incorrect date");
            valid = false;
        }
        if (tilHorsePower.getEditText().getText().toString().length() < 1) {
            tilHorsePower.setError("No horse power value");
            valid = false;
        }
        if (tilCubicCentimeters.getEditText().getText().toString().length() < 1) {
            tilHorsePower.setError("No cubic centimeters value");
            valid = false;
        }
        return valid;
    }

    private Vehicle getVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setName(tilName.getEditText().getText().toString());
        vehicle.setColor(Integer.parseInt(etColor.getText().toString()));
        vehicle.setRegistrationPlate(tilRegistrationPlate.getEditText().getText().toString());
        vehicle.setVinPlate(tilVinPlate.getEditText().getText().toString());
        vehicle.setOdometer(Long.parseLong(tilOdometer.getEditText().getText().toString()));
        vehicle.setHorsePower(Integer.parseInt(tilHorsePower.getEditText().getText().toString()));
        vehicle.setCubicCentimeter(Integer.parseInt(tilCubicCentimeters.getEditText().getText().toString()));
        try {
            Date manufactureDate = DateUtils.manufactureStringToDate(tilManufactureDate.getEditText()
                .getText().toString());
            vehicle.setManufactureDate(manufactureDate);
        } catch (ParseException e) {
            tilManufactureDate.setError("Incorrect date");
            e.printStackTrace();
            return null;
        }
        return vehicle;
    }

    private void saveToRealm(Vehicle vehicle) {
        if (vehicle != null) {
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
}
