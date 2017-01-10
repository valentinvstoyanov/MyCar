package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.widget.Spinner;
import android.widget.TextView;
import com.thebluealliance.spectrum.SpectrumDialog;
import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.dialogs.NewFuelTankDialog;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.FuelType;
import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.models.VehicleType;
import stoyanov.valentin.mycar.realm.repositories.IBrandRepository;
import stoyanov.valentin.mycar.realm.repositories.IModelRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.BrandRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.ModelRepository;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.ValidationUtils;

public class NewVehicleActivity extends BaseActivity{
    private Spinner spnVehicleType;
    private TextInputLayout tilName, tilBrand, tilModel, tilOdometer, tilHorsePower,
            tilCubicCentimeters, tilRegistrationPlate, tilVinPlate, tilNotes, tilManufactureDate;
    private Button btnAddFuelTank, btnColor;
    private LinearLayout llFuelTanks;
    private ArrayList<FuelTank> fuelTanks;
    private Realm myRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        initComponents();
        BrandRepository brandRepository = new BrandRepository();
        brandRepository.getAllBrands(new IBrandRepository.OnGetAllBrandsCallback() {
            @Override
            public void onSuccess(RealmResults<Brand> results) {
                String[] brandNames = BrandRepository.getBrandNames(results.toArray(new Brand[0]));
                ArrayAdapter<String> actvBrandsAdapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, brandNames);
                AutoCompleteTextView actvBrand = (AutoCompleteTextView) findViewById(R.id.actv_brand);
                actvBrand.setAdapter(actvBrandsAdapter);
            }
        });
        final ModelRepository modelRepository = new ModelRepository();
        modelRepository.getAllModels(new IModelRepository.OnGetAllModelsCallback() {
            @Override
            public void onSuccess(RealmResults<Model> results) {
                String[] modelNames = ModelRepository.getModelNames(results.toArray(new Model[0]));
                ArrayAdapter<String> actvModelsAdapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line, modelNames);
                AutoCompleteTextView actvModel = (AutoCompleteTextView) findViewById(R.id.actv_model);
                actvModel.setAdapter(actvModelsAdapter);
            }
        });
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
                .createFromResource(getApplicationContext(),
                        R.array.vehicle_types, R.layout.textview_spinner);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVehicleType.setAdapter(spinnerAdapter);
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
            if (isInputValid()) {
                saveToRealm(/*getVehicle()*/);
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
        llFuelTanks = (LinearLayout) findViewById(R.id.ll_new_vehicle_fuel_tanks);
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
        Calendar calendar = Calendar.getInstance();
        tilManufactureDate.getEditText()
                .setText(DateUtils.manufactureDateToString(calendar.getTime()));
        btnAddFuelTank = (Button) findViewById(R.id.btn_new_vehicle_add_ft);
        btnColor = (Button) findViewById(R.id.btn_new_vehicle_color);
        fuelTanks = new ArrayList<>();
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
                                calendar.set(year, month, day);
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
        btnAddFuelTank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFuelTank();
            }
        });
    }

    private void addFuelTank() {
        final NewFuelTankDialog fuelTankDialog = new NewFuelTankDialog();
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

    private void displayNewFuelTank(final FuelTank fuelTank) {
        View view = getLayoutInflater().inflate(R.layout.row_new_fuel_tank, llFuelTanks, false);
        TextView tvFuelTank = (TextView) view.findViewById(R.id.tv_row_ft_number);
        TextView tvFuelType = (TextView) view.findViewById(R.id.tv_row_ft_fuel_type);
        TextView tvFuelCapacity = (TextView) view.findViewById(R.id.tv_row_ft_capacity);
        TextView tvFuelConsumption = (TextView) view.findViewById(R.id.tv_row_ft_consumption);
        ImageButton imgBtnRemove = (ImageButton) view.findViewById(R.id.img_btn_remove);
        tvFuelTank.setText(getString(R.string.fuel_tank));
        String text = String.format(getString(R.string.fuel_type_placeholder),
                fuelTank.getFuelType().getName());
        tvFuelType.setText(text);
        text = String.format(getString(R.string.capacity_placeholder), fuelTank.getCapacity());
        tvFuelCapacity.setText(text);
        text = String.format(getString(R.string.consumption_placeholder), fuelTank.getConsumption());
        tvFuelConsumption.setText(text);
        imgBtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fuelTanks.remove(fuelTank);
                ((ViewGroup)view.getParent().getParent()).removeView((ViewGroup)view.getParent());
            }
        });
        llFuelTanks.addView(view);
    }

    public void colorPicker(View view) {
        SpectrumDialog.Builder builder = new SpectrumDialog.Builder(NewVehicleActivity.this);
        builder.setColors(R.array.vehicles_primary_colors);
        builder.setDismissOnColorSelected(true);
        ColorDrawable colorDrawable = (ColorDrawable) btnColor.getBackground();
        builder.setSelectedColor(colorDrawable.getColor());
        builder.setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                if (positiveResult) {
                    btnColor.setBackgroundColor(color);
                }
            }
        });
        builder.build().show(getSupportFragmentManager(), "color_picker");
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
        if (fuelTanks.size() < 1) {
            valid = false;
            Snackbar snackbar = Snackbar
                    .make(llFuelTanks, "No fuel tank added", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Add", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFuelTank();
                }
            });
            snackbar.show();
        }
        return valid;
    }

    private void saveToRealm() {
        myRealm = Realm.getDefaultInstance();
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle realmVehicle = realm.createObject(Vehicle.class,
                        UUID.randomUUID().toString());
                String vehicleTypeName = spnVehicleType.getSelectedItem().toString();
                VehicleType vehicleType = realm.where(VehicleType.class)
                        .equalTo(RealmTable.NAME, vehicleTypeName)
                        .findFirst();
                if (vehicleType == null) {
                    vehicleType = realm.createObject(VehicleType.class, UUID.randomUUID().toString());
                    vehicleType.setName(vehicleTypeName);
                }
                realmVehicle.setType(vehicleType);
                realmVehicle.setName(tilName.getEditText().getText().toString());
                try {
                    realmVehicle.setManufactureDate(DateUtils.manufactureStringToDate(tilManufactureDate.getEditText().getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ColorDrawable colorDrawable = (ColorDrawable) btnColor.getBackground();
                realmVehicle.setColor(colorDrawable.getColor());
                realmVehicle.setRegistrationPlate(tilRegistrationPlate.getEditText().getText().toString());
                realmVehicle.setVinPlate(tilVinPlate.getEditText().getText().toString());
                realmVehicle.setOdometer(Long.parseLong(tilOdometer.getEditText().getText().toString()));
                realmVehicle.setHorsePower(Integer.parseInt(tilHorsePower.getEditText().getText().toString()));
                realmVehicle.setCubicCentimeter(Integer.parseInt(tilCubicCentimeters.getEditText().getText().toString()));

                String brandName = tilBrand.getEditText().getText().toString();
                Brand brand = realm.where(Brand.class).equalTo(RealmTable.NAME, brandName).findFirst();
                if (brand == null) {
                    brand = realm.createObject(Brand.class, UUID.randomUUID().toString());
                    brand.setName(brandName);
                }
                realmVehicle.setBrand(brand);

                String modelName = tilModel.getEditText().getText().toString();
                Model model = realm.where(Model.class).equalTo(RealmTable.NAME, modelName).findFirst();
                if (model == null) {
                    model = realm.createObject(Model.class, UUID.randomUUID().toString());
                    model.setName(modelName);
                }
                realmVehicle.setModel(model);

                for (FuelTank fuelTank : fuelTanks) {
                    FuelTank realmFuelTank = realm.createObject(FuelTank.class,
                            UUID.randomUUID().toString());
                    realmFuelTank.setCapacity(fuelTank.getCapacity());
                    realmFuelTank.setConsumption(fuelTank.getConsumption());
                    String fuelTypeName = fuelTank.getFuelType().getName();
                    FuelType fuelType = realm.where(FuelType.class)
                            .equalTo(RealmTable.NAME, fuelTypeName).findFirst();
                    if (fuelType == null) {
                        fuelType = realm.createObject(FuelType.class, UUID.randomUUID().toString());
                        fuelType.setName(fuelTypeName);
                    }
                    realmFuelTank.setFuelType(fuelType);
                    realmVehicle.getFuelTanks().add(realmFuelTank);
                }

                String notes = tilNotes.getEditText().getText().toString();
                Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                note.setContent(notes);
                realmVehicle.setNote(note);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                myRealm.close();
                showMessage("New vehicle added!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                showMessage("Something went wrong...");
                new Exception(error).printStackTrace();
                myRealm.close();
            }
        });
    }
}
