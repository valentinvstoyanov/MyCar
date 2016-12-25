package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.View;
import android.widget.TextView;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IVehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleTypeRepository;
import stoyanov.valentin.mycar.utils.DateUtils;

public class ViewVehicleActivity extends BaseActivity {

    public static final String CAR_NAME = "car_name";
    private Toolbar toolbar;
    private TextView tvBrand, tvModel, tvOdometer,
                    tvHorsePower, tvCubicCentimeters,
                    tvManufactureDate, tvRegistrationPlate,
                    tvVinPlate, tvNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        initComponents();
        Intent intent = getIntent();
        String vehicleId = intent.getStringExtra(CAR_NAME);
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehicleRepository.getVehicleById(vehicleId, new IVehicleRepository.OnGetSigleVehicleCallback() {
            @Override
            public void onSuccess(Vehicle vehicle) {
                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.setBackgroundColor(vehicle.getColor());
                toolbar.setTitle(vehicle.getName());
                tvBrand.setText(vehicle.getBrand().getName());
                tvModel.setText(vehicle.getModel().getName());
                tvOdometer.setText(String.valueOf(vehicle.getOdometer()));
                tvHorsePower.setText(String.valueOf(vehicle.getHorsePower()));
                tvCubicCentimeters.setText(String.valueOf(vehicle.getCubicCentimeter()));
                tvManufactureDate.setText(DateUtils.manufactureDateToString(vehicle.getManufactureDate()));
                tvRegistrationPlate.setText(vehicle.getRegistrationPlate());
                tvVinPlate.setText(vehicle.getVinPlate());
                //notes
            }

            @Override
            public void onError() {
                toolbar.setTitle("Something went wrong ;(");
            }
        });
        setSupportActionBar(toolbar);
        setComponentListeners();
    }

    @Override
    protected void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvBrand = (TextView) findViewById(R.id.tv_cardview_brand);
        tvModel = (TextView) findViewById(R.id.tv_cardview_model);
        tvOdometer = (TextView) findViewById(R.id.tv_cardview_odometer);
        tvHorsePower = (TextView) findViewById(R.id.tv_cardview_horse_power);
        tvCubicCentimeters = (TextView) findViewById(R.id.tv_cardview_cubic_centimeters);
        tvManufactureDate = (TextView) findViewById(R.id.tv_cardview_manufacture_date);
        tvRegistrationPlate = (TextView) findViewById(R.id.tv_cardview_registration_plate);
        tvVinPlate = (TextView) findViewById(R.id.tv_cardview_vin_plate);
        tvNotes = (TextView) findViewById(R.id.tv_cardview_notes);
    }

    @Override
    protected void setComponentListeners() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_vehicle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
