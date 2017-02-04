package stoyanov.valentin.mycar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.preferences.PreferenceManager;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.Color;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.models.ExpenseType;
import stoyanov.valentin.mycar.realm.models.FuelType;
import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.RealmSettings;
import stoyanov.valentin.mycar.realm.models.ServiceType;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.models.VehicleType;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.ColorUtils;
import stoyanov.valentin.mycar.utils.CsvUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.ImageViewUtils;

public class WelcomeActivity extends BaseActivity
                        implements ViewPager.OnPageChangeListener{

    private PreferenceManager preferenceManager;
    private ViewPager viewPager;
    private Button buttonNext;
    private View tab1, tab2, tab3;
    private int[] sliders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext(), MODE_PRIVATE);
        if (!preferenceManager.isFirstLaunch()) {
            launchMainActivity();
            finish();
        }else {
            setContentView(R.layout.activity_welcome);
            setStatusBarColor(ResourcesCompat.getColor(getResources(), R.color.colorWelcome1, null));
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_realm_seeding);
            progressBar.setMax(100);
            progressBar.getIndeterminateDrawable().setColorFilter(
                    ResourcesCompat.getColor(getResources(),
                            R.color.colorAccent, null), PorterDuff.Mode.MULTIPLY);
            progressBar.setProgress(50);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    InputStream inputStream = getResources().openRawResource(R.raw.brands);
                    final String[] brandNames = CsvUtils.getParsedCsv(inputStream);
                    inputStream = getResources().openRawResource(R.raw.service_types);
                    final String[] serviceTypeNames = CsvUtils.getParsedCsv(inputStream);
                    inputStream = getResources().openRawResource(R.raw.companies);
                    final String[] companies = CsvUtils.getParsedCsv(inputStream);
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressBar.setProgress(20);
                    final String[] vehicleTypes = getResources().getStringArray(R.array.vehicle_types);
                    final String[] fuelTypes = getResources().getStringArray(R.array.fuel_types);
                    final String[] fuelUnit = getResources().getStringArray(R.array.fuel_units);
                    final TypedArray primaryColors = getResources().obtainTypedArray(R.array.vehicles_primary_colors);
                    final TypedArray darkColors = getResources().obtainTypedArray(R.array.vehicles_dark_colors);
                    final String[] expenseTypes = getResources().getStringArray(R.array.expense_types);
                    final Realm myRealm = Realm.getDefaultInstance();
                    myRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            for (String brandName : brandNames) {
                                Brand brand = realm.createObject(Brand.class,
                                        UUID.randomUUID().toString());
                                brand.setName(brandName);
                                Log.d("Brands", "execute: ");
                            }
                            progressBar.incrementProgressBy(10);
                            for (String typeName : serviceTypeNames) {
                                ServiceType serviceType = realm.createObject(ServiceType.class,
                                        UUID.randomUUID().toString());
                                serviceType.setName(typeName);
                                Log.d("Service types", "execute: ");
                            }
                            progressBar.incrementProgressBy(10);
                            for (String typeName : vehicleTypes) {
                                VehicleType vehicleType = realm.createObject(VehicleType.class,
                                        UUID.randomUUID().toString());
                                vehicleType.setName(typeName);
                                vehicleType.setDrawableName(ImageViewUtils
                                        .getDrawableNameByVehicleType(typeName));
                                Log.d("vehicle type", "execute: ");
                            }
                            progressBar.incrementProgressBy(10);
                            for (String companyName : companies) {
                                Company company = realm.createObject(Company.class,
                                        UUID.randomUUID().toString());
                                company.setName(companyName);
                                Log.d("Company", "execute: ");
                            }
                            progressBar.incrementProgressBy(10);
                            for (String fuelName : fuelTypes) {
                                FuelType fuelType = realm.createObject(FuelType.class,
                                        UUID.randomUUID().toString());
                                fuelType.setName(fuelName);
                                String unit;
                                if (fuelName.equals("Diesel") || fuelName.equals("Petrol")) {
                                    unit = fuelUnit[0];
                                } else {
                                    if (fuelName.equals("Electric")) {
                                        unit = fuelUnit[1];
                                    } else {
                                        unit = fuelUnit[2];
                                    }
                                }
                                fuelType.setUnit(unit);
                                Log.d("fuel type", "execute: ");
                            }
                            progressBar.incrementProgressBy(20);
                            for (int i = 0; i < primaryColors.length(); i++) {
                                Color color = realm.createObject(Color.class, UUID.randomUUID().toString());
                                color.setColor(primaryColors.getColor(i, 0));
                                color.setRelevantDarkColor(darkColors.getColor(i, 0));
                                color.setTextIconsColor(ColorUtils
                                        .pickColorByBackground(getApplicationContext(),
                                                color.getColor()));
                                Log.d("Color", "execute: ");
                            }
                            progressBar.incrementProgressBy(10);
                            for (String expenseTypeName : expenseTypes) {
                                ExpenseType expenseType = realm.createObject(ExpenseType.class,
                                        UUID.randomUUID().toString());
                                expenseType.setName(expenseTypeName);
                                Log.d("Expense type", "execute: ");
                            }
                            progressBar.incrementProgressBy(5);
                            RealmSettings settings = realm.createObject(RealmSettings.class,
                                    UUID.randomUUID().toString());
                            settings.setLengthUnit("km");
                            settings.setDistanceInAdvance(1000);
                            settings.setCurrencyUnit("BGN");
                            Log.d("Settings", "execute: ");
                            progressBar.incrementProgressBy(5);

                            for (int i = 0; i < 90; i++) {
                                Vehicle vehicle = realm.createObject(Vehicle.class, UUID.randomUUID().toString());
                                Color color = realm.where(Color.class).findAll().get(2);
                                vehicle.setColor(color);
                                VehicleType vehicleType = realm.where(VehicleType.class)
                                        .equalTo(RealmTable.NAME, "Car")
                                        .findFirst();
                                vehicle.setType(vehicleType);
                                vehicle.setName(String.valueOf(Math.abs(new Random().nextInt())));
                                vehicle.setManufactureDate(DateUtils.stringToDate("22.08.2016"));
                                vehicle.setRegistrationPlate("asdasdsa");
                                vehicle.setVinPlate("asdasdsa");
                                vehicle.setOdometer(455);
                                vehicle.setHorsePower(44);
                                vehicle.setCubicCentimeter(1400);

                                String brandName = "Abarth";
                                Brand brand = realm.where(Brand.class).equalTo(RealmTable.NAME, brandName).findFirst();
                                if (brand == null) {
                                    brand = realm.createObject(Brand.class, UUID.randomUUID().toString());
                                    brand.setName(brandName);
                                }
                                vehicle.setBrand(brand);

                                String modelName = "A10";
                                Model model = realm.where(Model.class).equalTo(RealmTable.NAME, modelName).findFirst();
                                if (model == null) {
                                    model = realm.createObject(Model.class, UUID.randomUUID().toString());
                                    model.setName(modelName);
                                }
                                vehicle.setModel(model);
                                String notes = "asdasdasdsadasdasdadjfhiweo;lkhqwit;qiowrelnfashdfiosdlcHIDOLhISOclZHDIasdihaosdlasdhinxzhOIxalsnaodlasdadalndkasdasdaldashidolnadhoialdashidoasdlnadhioasldahiodasldahsiodlahdiolnad";
                                Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                                note.setContent(notes);
                                vehicle.setNote(note);
                                Log.d("Vehicles", "execute: ");
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            myRealm.close();
                            primaryColors.recycle();
                            darkColors.recycle();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            error.printStackTrace();
                            myRealm.close();
                            primaryColors.recycle();
                            darkColors.recycle();
                        }
                    });
                    /*myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                        }
                    });*/
                    /*myRealm.close();*/
                }
            });
            thread.run();
            initComponents();
            setComponentListeners();
            WelcomeViewPagerAdapter viewPagerAdapter = new WelcomeViewPagerAdapter();
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.addOnPageChangeListener(this);
            addTabColors(0);
        }
    }

    @Override
    public void initComponents() {
        viewPager = (ViewPager) findViewById(R.id.vp_welcome);
        buttonNext = (Button) findViewById(R.id.btn_welcome_next);
        tab1 = findViewById(R.id.view_welcome1);
        tab2 = findViewById(R.id.view_welcome2);
        tab3 = findViewById(R.id.view_welcome3);
        sliders = new int[] {
                R.layout.slide_welcome1,
                R.layout.slide_welcome2,
                R.layout.slide_welcome3
        };
    }

    @Override
    public void setComponentListeners() {
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewPagerCurrentSlider = viewPager.getCurrentItem() + 1;
                if (viewPagerCurrentSlider < sliders.length) {
                    viewPager.setCurrentItem(viewPagerCurrentSlider);
                }else {
                    launchMainActivity();
                    finish();
                }
            }
        });
    }

    @Override
    public void setContent() {

    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    private void launchMainActivity() {
        preferenceManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void addTabColors(int currentSlider) {
        int[] activeColors = getResources().getIntArray(R.array.activeColors);
        int[] inactiveColors = getResources().getIntArray(R.array.inactiveColors);
        switch (currentSlider) {
            case 0:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome1, null));
                tab1.setBackgroundColor(activeColors[currentSlider]);
                tab2.setBackgroundColor(inactiveColors[currentSlider]);
                tab3.setBackgroundColor(inactiveColors[currentSlider]);
                break;
            case 1:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome2, null));
                tab1.setBackgroundColor(inactiveColors[currentSlider]);
                tab2.setBackgroundColor(activeColors[currentSlider]);
                tab3.setBackgroundColor(inactiveColors[currentSlider]);
                break;
            case 2:
                setStatusBarColor(ResourcesCompat.getColor(getResources(),
                        R.color.colorWelcome3, null));
                tab1.setBackgroundColor(inactiveColors[currentSlider]);
                tab2.setBackgroundColor(inactiveColors[currentSlider]);
                tab3.setBackgroundColor(activeColors[currentSlider]);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        addTabColors(position);
        int currentPosition = position + 1;
        if (currentPosition == sliders.length) {
            buttonNext.setText(getString(R.string.got_it));
        }else {
            buttonNext.setText(getString(R.string.next));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class WelcomeViewPagerAdapter extends PagerAdapter{

        private LayoutInflater inflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(sliders[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return sliders.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
