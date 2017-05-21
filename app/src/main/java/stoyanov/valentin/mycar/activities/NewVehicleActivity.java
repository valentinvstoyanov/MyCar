package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.thebluealliance.spectrum.SpectrumDialog;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.activities.interfaces.INewBaseActivity;
import stoyanov.valentin.mycar.dialogs.NewFuelTankDialog;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.Color;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.ColorUtils;
import stoyanov.valentin.mycar.utils.DateTimePickerUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.TextUtils;
import stoyanov.valentin.mycar.utils.ValidationUtils;

public class NewVehicleActivity extends BaseActivity implements INewBaseActivity {

    private Spinner spnVehicleType;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private TextInputLayout tilName;
    private TextInputLayout tilBrand;
    private TextInputLayout tilModel;
    private TextInputLayout tilHorsePower;
    private TextInputLayout tilCubicCentimeters;
    private TextInputLayout tilRegistrationPlate;
    private TextInputLayout tilVinPlate;
    private Button btnManufactureDate;
    private Button btnColor;
    private Button btnAddFuelTank;
    private LinearLayout llFuelTanks;
    private ArrayList<FuelTank> fuelTanks;
    private ArrayList<FuelTank> existingFuelTanks;
    private String vehicleId;
    private int vehicleColor;
    private Vehicle vehicle;
    private Realm myRealm;
    private TextInputLayout tilOdometer;
    private TextInputLayout tilNote;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackNavigation();
        initComponents();
        setContent();
        setComponentListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            progressBar.setIndeterminate(true);
            if (isInputValid()) {
                saveToRealm();
            }else {
                progressBar.setIndeterminate(false);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void initComponents() {
        spnVehicleType = (Spinner) findViewById(R.id.spn_new_vehicle_type);
        tilName = (TextInputLayout) findViewById(R.id.til_new_vehicle_name);
        tilBrand = (TextInputLayout) findViewById(R.id.til_new_vehicle_brand);
        tilModel = (TextInputLayout) findViewById(R.id.til_new_vehicle_model);
        tilHorsePower = (TextInputLayout) findViewById(R.id.til_new_vehicle_horse_power);
        tilCubicCentimeters = (TextInputLayout) findViewById(R.id.til_new_vehicle_cubic_centimeters);
        tilRegistrationPlate = (TextInputLayout) findViewById(R.id.til_new_vehicle_registration_plate);
        tilVinPlate = (TextInputLayout) findViewById(R.id.til_new_vehicle_vin_plate);
        llFuelTanks = (LinearLayout) findViewById(R.id.ll_new_vehicle_fuel_tanks);
        btnAddFuelTank = (Button) findViewById(R.id.btn_new_vehicle_add_fuel_tank);
        btnColor = (Button) findViewById(R.id.btn_new_vehicle_color);
        btnManufactureDate = (Button) findViewById(R.id.btn_new_vehicle_manufacture_date);
        tilOdometer = (TextInputLayout) findViewById(R.id.til_odometer);
        tilNote = (TextInputLayout) findViewById(R.id.til_note);
        spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.vehicle_types, R.layout.textview_spinner);
        fuelTanks = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.pb_new_vehicle);
        vehicleId = getIntent().getStringExtra(Constants.ID);
        myRealm = Realm.getDefaultInstance();
    }

    @Override
    public void setComponentListeners() {
        btnAddFuelTank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFuelTank();
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpectrumDialog.Builder builder = new SpectrumDialog.Builder(NewVehicleActivity.this);
                builder.setColors(R.array.vehicles_primary_colors);
                builder.setDismissOnColorSelected(true);
                builder.setSelectedColor(vehicleColor);
                builder.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                        if (positiveResult) {
                            btnColor.setTextColor(color);
                            vehicleColor = color;
                        }
                    }
                });
                builder.build().show(getSupportFragmentManager(), "color_picker");
            }
        });

        btnManufactureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerUtils.showDatePicker(NewVehicleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = DateUtils.getDateFromInts(year, month, dayOfMonth);
                        btnManufactureDate.setText(date);
                    }
                });
            }
        });
    }

    @Override
    public void setContent() {
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVehicleType.setAdapter(spinnerAdapter);

        ArrayAdapter<String> brandsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getBrandNames());
        AutoCompleteTextView actvBrand = (AutoCompleteTextView) findViewById(R.id.actv_brand);
        actvBrand.setAdapter(brandsAdapter);

        ArrayAdapter<String> modelsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getModelNames());
        AutoCompleteTextView actvModel = (AutoCompleteTextView) findViewById(R.id.actv_model);
        actvModel.setAdapter(modelsAdapter);

        if (isNewVehicle()) {
            vehicleColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
        }else {
            vehicle = myRealm.where(Vehicle.class)
                    .equalTo(Constants.ID, vehicleId)
                    .findFirst();
            setToolbarTitle("Edit vehicle");
            spnVehicleType.setSelection(spinnerAdapter.getPosition(vehicle.getType()));
            TextUtils.setTextToTil(tilName, vehicle.getName());
            TextUtils.setTextToAutoComplete(tilBrand, vehicle.getBrand().getName());
            TextUtils.setTextToAutoComplete(tilModel, vehicle.getModel().getName());
            TextUtils.setTextToTil(tilOdometer, String.valueOf(vehicle.getOdometer()));
            TextUtils.setTextToTil(tilHorsePower, String.valueOf(vehicle.getHorsePower()));
            TextUtils.setTextToTil(tilCubicCentimeters, String.valueOf(vehicle.getCubicCentimeter()));
            TextUtils.setTextToTil(tilRegistrationPlate, vehicle.getRegistrationPlate());
            TextUtils.setTextToTil(tilVinPlate, vehicle.getVinPlate());
            TextUtils.setTextToTil(tilNote, vehicle.getNote());
            btnManufactureDate.setText(DateUtils.dateToString(vehicle.getManufactureDate()));
            vehicleColor = vehicle.getColor().getColor();
            existingFuelTanks = new ArrayList<>(vehicle.getFuelTanks().size());
            for (FuelTank fuelTank : vehicle.getFuelTanks()) {
                existingFuelTanks.add(fuelTank);
                displayNewFuelTank(fuelTank);
            }
        }
        btnColor.setTextColor(vehicleColor);
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;

        if (!ValidationUtils.isInputValid(TextUtils.getTextFromTil(tilName))) {
            tilName.setError("Incorrect vehicle name");
            valid = false;
        }

        if (!ValidationUtils.isInputValid(TextUtils.getTextFromAutoComplete(tilBrand))) {
            tilBrand.setError("Incorrect vehicle brand");
            valid = false;
        }

        if (!ValidationUtils.isInputValid(TextUtils.getTextFromAutoComplete(tilModel))) {
            tilModel.setError("Incorrect vehicle model");
            valid = false;
        }

        if(!ValidationUtils.isInputValid(TextUtils.getTextFromTil(tilRegistrationPlate))) {
            tilRegistrationPlate.setError("Incorrect registration plate");
            valid = false;
        }

        if (!ValidationUtils.isInputValid(TextUtils.getTextFromTil(tilVinPlate))) {
            tilVinPlate.setError("Incorrect VIN number");
            valid = false;
        }

        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilHorsePower))) {
            tilHorsePower.setError("No horse power value");
            valid = false;
        }else {
            if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilHorsePower)) <
                    NumberUtils.INTEGER_ZERO) {
                tilHorsePower.setError("Horse power should not be negative");
                valid = false;
            }
        }

        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilCubicCentimeters))) {
            tilCubicCentimeters.setError("No cubic centimeter value");
            valid = false;
        }else {
            if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilCubicCentimeters)) <
                    NumberUtils.INTEGER_ZERO) {
                tilCubicCentimeters.setError("Cubic centimeters should not be negative");
                valid = false;
            }
        }

        if ((fuelTanks.isEmpty() && (existingFuelTanks == null || existingFuelTanks.isEmpty()))) {
            valid = false;
            Snackbar snackbar = Snackbar
                    .make(llFuelTanks, "No fuel tank added", Snackbar.LENGTH_LONG);
            snackbar.setAction("Add", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFuelTank();
                }
            });
            snackbar.show();
        }

        if (DateUtils.isNotValidDate(btnManufactureDate.getText().toString(), false)) {
            valid = false;
            showMessage("Invalid date");
        }

        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilOdometer))) {
            valid = false;
            tilOdometer.setError("Numeric value expected");
        }else {
            if (NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer)) < NumberUtils.LONG_ZERO) {
                valid = false;
                tilOdometer.setError("Odometer cannot be negative");
            }
        }

        if (TextUtils.getTextFromTil(tilNote).length() > 256) {
            valid = false;
            tilNote.setError("Too many characters");
        }

        if (!isNewVehicle()) {
            long odometer = vehicle.getOdometer();
            if (Long.parseLong(TextUtils.getTextFromTil(tilOdometer)) < odometer) {
                valid = false;
                tilOdometer.setError("Odometer can't be decreased, current: " + odometer);
            }
        }

        return valid;
    }

    @Override
    public void saveToRealm() {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle vehicle;
                if (isNewVehicle()) {
                    vehicle = realm.createObject(Vehicle.class, UUID.randomUUID().toString());
                }else {
                    vehicle = realm.where(Vehicle.class)
                            .equalTo(Constants.ID, vehicleId)
                            .findFirst();
                }

                String typeText = spnVehicleType.getSelectedItem().toString();
                vehicle.setType(typeText);

                vehicle.setName(TextUtils.getTextFromTil(tilName));
                vehicle.setManufactureDate(DateUtils.stringToDate(btnManufactureDate.getText().toString()));

                Color color = realm.where(Color.class)
                        .equalTo(Constants.COLOR, vehicleColor)
                        .findFirst();
                if (color == null) {
                    color = realm.createObject(Color.class, UUID.randomUUID().toString());
                    color.setColor(vehicleColor);
                    color.setRelevantDarkColor(ColorUtils.getDarkColor(getApplicationContext(),
                            vehicleColor));
                    color.setTextIconsColor(ColorUtils.pickColorByBackground(getApplicationContext(),
                            vehicleColor));
                }
                vehicle.setColor(color);

                vehicle.setRegistrationPlate(TextUtils.getTextFromTil(tilRegistrationPlate));
                vehicle.setVinPlate(TextUtils.getTextFromTil(tilVinPlate));
                vehicle.setOdometer(NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer)));
                vehicle.setHorsePower(NumberUtils.createInteger(TextUtils.getTextFromTil(tilHorsePower)));
                vehicle.setCubicCentimeter(NumberUtils.createInteger(TextUtils.getTextFromTil(tilCubicCentimeters)));

                String brandText = TextUtils.getTextFromAutoComplete(tilBrand);
                Brand brand = realm.where(Brand.class)
                        .equalTo(Constants.NAME, brandText)
                        .findFirst();
                if (brand == null) {
                    brand = realm.createObject(Brand.class, UUID.randomUUID().toString());
                    brand.setName(brandText);
                }
                vehicle.setBrand(brand);

                String modelText = TextUtils.getTextFromAutoComplete(tilModel);
                Model model = realm.where(Model.class)
                        .equalTo(Constants.NAME, modelText)
                        .findFirst();
                if (model == null) {
                    model = realm.createObject(Model.class, UUID.randomUUID().toString());
                    model.setName(modelText);
                }
                vehicle.setModel(model);

                for (FuelTank fuelTank : fuelTanks) {
                    FuelTank realmFuelTank = realm.createObject(FuelTank.class,
                            UUID.randomUUID().toString());
                    realmFuelTank.setCapacity(fuelTank.getCapacity());
                    realmFuelTank.setConsumption(fuelTank.getConsumption());
                    String fuelTypeText = fuelTank.getType();
                    if (fuelTank.getType().charAt(0) == 'E') {
                        realmFuelTank.setUnit("kwh");
                    }else {
                        realmFuelTank.setUnit("L");
                    }
                    realmFuelTank.setType(fuelTypeText);
                    vehicle.getFuelTanks().add(realmFuelTank);
                }

                vehicle.setNote(TextUtils.getTextFromTil(tilNote));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                progressBar.setIndeterminate(false);
                if (isNewVehicle()) {
                    showMessage("New vehicle added!");
                }else {
                    showMessage("Vehicle updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewVehicleActivity.class);
                    intent.putExtra(Constants.ID, vehicleId);
                    startActivity(intent);
                }
                finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                progressBar.setIndeterminate(false);
                showMessage("Something went wrong...");
                error.printStackTrace();
            }
        });
    }

    private boolean isNewVehicle() {
        return vehicleId == null;
    }

    private String[] getBrandNames() {
        RealmResults<Brand> brands = myRealm.where(Brand.class).findAll();
        String[] brandNames = new String[brands.size()];
        int i = 0;
        for (Brand brand : brands) {
            brandNames[i] = brand.getName();
            i++;
        }
        return brandNames;
    }

    private String[] getModelNames() {
        RealmResults<Model> models = myRealm.where(Model.class).findAll();
        String[] modelNames = new String[models.size()];
        int i = 0;
        for (Model model : models) {
            modelNames[i] = model.getName();
            i++;
        }
        return modelNames;
    }

    private ArrayList<String> getFuelTankFuelTypes() {
        ArrayList<String> fuelTypes = new ArrayList<>();
        for (FuelTank fuelTank : fuelTanks) {
            fuelTypes.add(fuelTank.getType());
        }
        if (existingFuelTanks != null) {
            for (FuelTank fuelTank : existingFuelTanks) {
                fuelTypes.add(fuelTank.getType());
            }
        }
        return fuelTypes;
    }

    private void displayNewFuelTank(final FuelTank fuelTank) {
        View view = getLayoutInflater().inflate(R.layout.row_new_fuel_tank, llFuelTanks, false);
        TextView tvFuelTank = (TextView) view.findViewById(R.id.tv_row_ft_number);
        TextView tvFuelType = (TextView) view.findViewById(R.id.tv_row_ft_fuel_type);
        TextView tvFuelCapacity = (TextView) view.findViewById(R.id.tv_row_ft_capacity);
        TextView tvFuelConsumption = (TextView) view.findViewById(R.id.tv_row_ft_consumption);
        ImageButton imgBtnRemove = (ImageButton) view.findViewById(R.id.img_btn_remove);
        tvFuelTank.setText(getString(R.string.fuel_tank));
        String text = String.format(getString(R.string.fuel_type_placeholder), fuelTank.getType());
        tvFuelType.setText(text);
        text = String.format(getString(R.string.capacity_placeholder), fuelTank.getCapacity());
        tvFuelCapacity.setText(text);
        text = String.format(getString(R.string.consumption_placeholder), fuelTank.getConsumption());
        tvFuelConsumption.setText(text);
        imgBtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fuelTank.getId() != null) {
                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            int position = existingFuelTanks.indexOf(fuelTank);
                            existingFuelTanks.remove(fuelTank);
                            vehicle.getFuelTanks().deleteFromRealm(position);
                        }
                    });
                }else {
                    fuelTanks.remove(fuelTank);
                }
                ((ViewGroup)view.getParent().getParent()).removeView((ViewGroup)view.getParent());
            }
        });
        llFuelTanks.addView(view);
    }

    private void addFuelTank() {
        ArrayList<String> fuelTypes = getFuelTankFuelTypes();
        ArrayList<String> fuelTypesRes = new ArrayList<>(Arrays
                .asList(getResources().getStringArray(R.array.fuel_types)));
        if (fuelTypes.size() == fuelTypesRes.size()) {
            showMessage("You have all possible fuel tank types");
        }else {
            fuelTypesRes.removeAll(fuelTypes);
            final NewFuelTankDialog fuelTankDialog = new NewFuelTankDialog();
            fuelTankDialog.setPossibleFuelTypes(fuelTypesRes);
            fuelTankDialog.setListener(new NewFuelTankDialog.OnAddFuelTankListener() {
                @Override
                public void onAddFuelTank(FuelTank fuelTank) {
                    fuelTankDialog.dismiss();
                    fuelTanks.add(fuelTank);
                    displayNewFuelTank(fuelTank);
                }
            });
            fuelTankDialog.show(getSupportFragmentManager(), getString(R.string.new_fuel_tank));
        }
    }

  /*  private Spinner spnVehicleType;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private TextInputLayout tilName, tilBrand, tilModel, tilHorsePower;
    private TextInputLayout tilCubicCentimeters, tilRegistrationPlate, tilVinPlate;
    private Button btnAddFuelTank;
    private ImageButton btnColor;
    private int colorValue;
    private LinearLayout llFuelTanks;
    private ArrayList<FuelTank> fuelTanks;
    private ArrayList<FuelTank> existingFuelTanks;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        initComponents();
        setContent();
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
        if (getVehicleId() != null) {
            setUpdate(true);
        }
        spnVehicleType = (Spinner) findViewById(R.id.spn_new_vehicle_type);
        tilName = (TextInputLayout) findViewById(R.id.til_new_vehicle_name);
        tilBrand = (TextInputLayout) findViewById(R.id.til_new_vehicle_brand);
        tilModel = (TextInputLayout) findViewById(R.id.til_new_vehicle_model);
        tilHorsePower = (TextInputLayout) findViewById(R.id.til_new_vehicle_horse_power);
        tilCubicCentimeters = (TextInputLayout) findViewById(R.id.til_new_vehicle_cubic_centimeters);
        tilRegistrationPlate = (TextInputLayout) findViewById(R.id.til_new_vehicle_registration_plate);
        tilVinPlate = (TextInputLayout) findViewById(R.id.til_new_vehicle_vin_plate);
        llFuelTanks = (LinearLayout) findViewById(R.id.ll_new_vehicle_fuel_tanks);
        btnAddFuelTank = (Button) findViewById(R.id.btn_new_vehicle_add_ft);
        btnColor = (ImageButton) findViewById(R.id.img_btn_new_vehicle_color);
        spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.vehicle_types, R.layout.textview_spinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVehicleType.setAdapter(spinnerAdapter);
        fuelTanks = new ArrayList<>();
        ArrayAdapter<String> brandsAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, getBrandNames());
        AutoCompleteTextView actvBrand = (AutoCompleteTextView) findViewById(R.id.actv_brand);
        actvBrand.setAdapter(brandsAdapter);
        ArrayAdapter<String> modelsAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, getModelNames());
        AutoCompleteTextView actvModel = (AutoCompleteTextView) findViewById(R.id.actv_model);
        actvModel.setAdapter(modelsAdapter);
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        btnAddFuelTank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFuelTank();
            }
        });
    }

    @Override
    public void setContent() {
        tilDate.setHint(getString(R.string.manufacture_date));
        colorValue = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
        btnColor.setColorFilter(colorValue);
        if (isUpdate()) {
            vehicle = myRealm.where(Vehicle.class)
                    .equalTo(Constants.ID, getVehicleId())
                    .findFirst();
            setToolbarTitle("Edit vehicle");
            spnVehicleType.setSelection(spinnerAdapter.getPosition(vehicle.getType()));
            TextUtils.setTextToTil(tilName, vehicle.getName());
            TextUtils.setTextToAutoComplete(tilBrand, vehicle.getBrand().getName());
            TextUtils.setTextToAutoComplete(tilModel, vehicle.getModel().getName());
            TextUtils.setTextToTil(tilOdometer, String.valueOf(vehicle.getOdometer()));
            TextUtils.setTextToTil(tilHorsePower, String.valueOf(vehicle.getHorsePower()));
            TextUtils.setTextToTil(tilCubicCentimeters, String.valueOf(vehicle.getCubicCentimeter()));
            TextUtils.setTextToTil(tilRegistrationPlate, vehicle.getRegistrationPlate());
            TextUtils.setTextToTil(tilVinPlate, vehicle.getVinPlate());
            TextUtils.setTextToTil(tilNote, vehicle.getNote());
            TextUtils.setTextToTil(tilDate, DateUtils.dateToString(vehicle.getManufactureDate()));
            btnColor.setColorFilter(vehicle.getColor().getColor());
            existingFuelTanks = new ArrayList<>(vehicle.getFuelTanks().size());
            for (FuelTank fuelTank : vehicle.getFuelTanks()) {
                existingFuelTanks.add(fuelTank);
                displayNewFuelTank(fuelTank);
            }
        }
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;
        if (!ValidationUtils.isInputValid(TextUtils.getTextFromTil(tilName))) {
            tilName.setError("Incorrect vehicle name");
            valid = false;
        }
        if (!ValidationUtils.isInputValid(TextUtils.getTextFromAutoComplete(tilBrand))) {
            tilBrand.setError("Incorrect vehicle brand");
            valid = false;
        }
        if (!ValidationUtils.isInputValid(TextUtils.getTextFromAutoComplete(tilModel))) {
            tilModel.setError("Incorrect vehicle model");
            valid = false;
        }
        if(!ValidationUtils.isInputValid(TextUtils.getTextFromTil(tilRegistrationPlate))) {
            tilRegistrationPlate.setError("Incorrect registration plate");
            valid = false;
        }
        if (!ValidationUtils.isInputValid(TextUtils.getTextFromTil(tilVinPlate))) {
            tilVinPlate.setError("Incorrect VIN number");
            valid = false;
        }
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilHorsePower))) {
            tilHorsePower.setError("No horse power value");
            valid = false;
        }else {
            if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilHorsePower)) <
                    NumberUtils.INTEGER_ZERO) {
                tilHorsePower.setError("Horse power should not be negative");
                valid = false;
            }
        }
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilCubicCentimeters))) {
            tilCubicCentimeters.setError("No cubic centimeter value");
            valid = false;
        }else {
            if (NumberUtils.createInteger(TextUtils.getTextFromTil(tilCubicCentimeters)) <
                    NumberUtils.INTEGER_ZERO) {
                tilCubicCentimeters.setError("Cubic centimeters should not be negative");
                valid = false;
            }
        }
        if ((fuelTanks.isEmpty() && (existingFuelTanks == null || existingFuelTanks.isEmpty()))) {
            valid = false;
            Snackbar snackbar = Snackbar
                    .make(llFuelTanks, "No fuel tank added", Snackbar.LENGTH_LONG);
            snackbar.setAction("Add", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFuelTank();
                }
            });
            snackbar.show();
        }
        if (isUpdate()) {
            long odometer = vehicle.getOdometer();
            if (Long.parseLong(TextUtils.getTextFromTil(tilOdometer)) < odometer) {
                valid = false;
                tilOdometer.setError("Odometer can't be decreased, current: " + odometer);
            }
        }
        return super.isInputValid() && valid;
    }

    @Override
    public void saveToRealm() {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle vehicle;
                if (isUpdate()) {
                    vehicle = realm.where(Vehicle.class)
                            .equalTo(Constants.ID, getVehicleId())
                            .findFirst();
                }else {
                    vehicle = realm.createObject(Vehicle.class, UUID.randomUUID().toString());
                }

                String typeText = spnVehicleType.getSelectedItem().toString();
                vehicle.setType(typeText);

                vehicle.setName(TextUtils.getTextFromTil(tilName));
                vehicle.setManufactureDate(DateUtils.stringToDate(TextUtils.getTextFromTil(tilDate)));

                Color color = realm.where(Color.class)
                        .equalTo(Constants.COLOR, colorValue)
                        .findFirst();
                if (color == null) {
                    color = realm.createObject(Color.class, UUID.randomUUID().toString());
                    color.setColor(colorValue);
                    color.setRelevantDarkColor(ColorUtils.getDarkColor(getApplicationContext(),
                            colorValue));
                    color.setTextIconsColor(ColorUtils.pickColorByBackground(getApplicationContext(),
                            colorValue));
                }
                vehicle.setColor(color);

                vehicle.setRegistrationPlate(TextUtils.getTextFromTil(tilRegistrationPlate));
                vehicle.setVinPlate(TextUtils.getTextFromTil(tilVinPlate));
                vehicle.setOdometer(NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer)));
                vehicle.setHorsePower(NumberUtils.createInteger(TextUtils.getTextFromTil(tilHorsePower)));
                vehicle.setCubicCentimeter(NumberUtils.createInteger(TextUtils.getTextFromTil(tilCubicCentimeters)));

                String brandText = TextUtils.getTextFromAutoComplete(tilBrand);
                Brand brand = realm.where(Brand.class)
                        .equalTo(Constants.NAME, brandText)
                        .findFirst();
                if (brand == null) {
                    brand = realm.createObject(Brand.class, UUID.randomUUID().toString());
                    brand.setName(brandText);
                }
                vehicle.setBrand(brand);

                String modelText = TextUtils.getTextFromAutoComplete(tilModel);
                Model model = realm.where(Model.class)
                        .equalTo(Constants.NAME, modelText)
                        .findFirst();
                if (model == null) {
                    model = realm.createObject(Model.class, UUID.randomUUID().toString());
                    model.setName(modelText);
                }
                vehicle.setModel(model);

                for (FuelTank fuelTank : fuelTanks) {
                    FuelTank realmFuelTank = realm.createObject(FuelTank.class,
                            UUID.randomUUID().toString());
                    realmFuelTank.setCapacity(fuelTank.getCapacity());
                    realmFuelTank.setConsumption(fuelTank.getConsumption());
                    String fuelTypeText = fuelTank.getType();
                    realmFuelTank.setType(fuelTypeText);
                    vehicle.getFuelTanks().add(realmFuelTank);
                }

                vehicle.setNote(TextUtils.getTextFromTil(tilNote));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isUpdate()) {
                    showMessage("Vehicle updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewVehicleActivity.class);
                    intent.putExtra(Constants.ID, getVehicleId());
                    startActivity(intent);
                }else {
                    showMessage("New vehicle added!");
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

    public void colorPicker(View view) {
        SpectrumDialog.Builder builder = new SpectrumDialog.Builder(NewVehicleActivity.this);
        builder.setColors(R.array.vehicles_primary_colors);
        builder.setDismissOnColorSelected(true);
        builder.setSelectedColor(colorValue);
        builder.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                if (positiveResult) {
                    btnColor.setColorFilter(color);
                    colorValue = color;
                }
            }
        });
        builder.build().show(getSupportFragmentManager(), "color_picker");
    }

    private String[] getBrandNames() {
        RealmResults<Brand> brands = myRealm.where(Brand.class).findAll();
        String[] brandNames = new String[brands.size()];
        int i = 0;
        for (Brand brand : brands) {
            brandNames[i] = brand.getName();
            i++;
        }
        return brandNames;
    }

    private String[] getModelNames() {
        RealmResults<Model> models = myRealm.where(Model.class).findAll();
        String[] modelNames = new String[models.size()];
        int i = 0;
        for (Model model : models) {
            modelNames[i] = model.getName();
            i++;
        }
        return modelNames;
    }

    private ArrayList<String> getFuelTankFuelTypes() {
        ArrayList<String> fuelTypes = new ArrayList<>();
        for (FuelTank fuelTank : fuelTanks) {
            fuelTypes.add(fuelTank.getType());
        }
        if (existingFuelTanks != null) {
            for (FuelTank fuelTank : existingFuelTanks) {
                fuelTypes.add(fuelTank.getType());
            }
        }
        return fuelTypes;
    }

    private void addFuelTank() {
        ArrayList<String> fuelTypes = getFuelTankFuelTypes();
        ArrayList<String> fuelTypesRes = new ArrayList<>(Arrays
                .asList(getResources().getStringArray(R.array.fuel_types)));
        if (fuelTypes.size() == fuelTypesRes.size()) {
            showMessage("You have all possible fuel tank types");
        }else {
            fuelTypesRes.removeAll(fuelTypes);
            final NewFuelTankDialog fuelTankDialog = new NewFuelTankDialog();
            fuelTankDialog.setPossibleFuelTypes(fuelTypesRes);
            fuelTankDialog.setListener(new NewFuelTankDialog.OnAddFuelTankListener() {
                @Override
                public void onAddFuelTank(FuelTank fuelTank) {
                    fuelTankDialog.dismiss();
                    fuelTanks.add(fuelTank);
                    displayNewFuelTank(fuelTank);
                }
            });
            fuelTankDialog.show(getSupportFragmentManager(), getString(R.string.new_fuel_tank));
        }
    }

    private void displayNewFuelTank(final FuelTank fuelTank) {
        View view = getLayoutInflater().inflate(R.layout.row_new_fuel_tank, llFuelTanks, false);
        TextView tvFuelTank = (TextView) view.findViewById(R.id.tv_row_ft_number);
        TextView tvFuelType = (TextView) view.findViewById(R.id.tv_row_ft_fuel_type);
        TextView tvFuelCapacity = (TextView) view.findViewById(R.id.tv_row_ft_capacity);
        TextView tvFuelConsumption = (TextView) view.findViewById(R.id.tv_row_ft_consumption);
        ImageButton imgBtnRemove = (ImageButton) view.findViewById(R.id.img_btn_remove);
        tvFuelTank.setText(getString(R.string.fuel_tank));
        String text = String.format(getString(R.string.fuel_type_placeholder), fuelTank.getType());
        tvFuelType.setText(text);
        text = String.format(getString(R.string.capacity_placeholder), fuelTank.getCapacity());
        tvFuelCapacity.setText(text);
        text = String.format(getString(R.string.consumption_placeholder), fuelTank.getConsumption());
        tvFuelConsumption.setText(text);
        imgBtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fuelTank.getId() != null) {
                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            int position = existingFuelTanks.indexOf(fuelTank);
                            existingFuelTanks.remove(fuelTank);
                            vehicle.getFuelTanks()
                                    .deleteFromRealm(position);
                        }
                    });
                }else {
                    fuelTanks.remove(fuelTank);
                }
                ((ViewGroup)view.getParent().getParent()).removeView((ViewGroup)view.getParent());
            }
        });
        llFuelTanks.addView(view);
    }*/
}
