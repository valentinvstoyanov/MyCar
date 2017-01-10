package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;

public class ViewVehicleActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView tvBrand, tvModel, tvOdometer, tvManufactureDate;
    private TextView tvHorsePower, tvCubicCentimeters, tvRegistrationPlate;
    private TextView tvVinPlate, tvNotes;
    private String vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        initComponents();
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
        Realm myRealm = Realm.getDefaultInstance();
        Vehicle vehicle = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, vehicleId)
                .findFirst();
        myRealm.close();
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setBackgroundColor(vehicle.getColor());
        toolbar.setTitle(vehicle.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvBrand.setText(vehicle.getBrand().getName());
        tvModel.setText(vehicle.getModel().getName());
        tvOdometer.setText(String.valueOf(vehicle.getOdometer()));
        tvManufactureDate.setText(DateUtils.manufactureDateToString(vehicle.getManufactureDate()));
        tvHorsePower.setText(String.valueOf(vehicle.getHorsePower()));
        tvCubicCentimeters.setText(String.valueOf(vehicle.getCubicCentimeter()));
        tvRegistrationPlate.setText(vehicle.getRegistrationPlate());
        tvVinPlate.setText(vehicle.getVinPlate());
        tvNotes.setText(vehicle.getNote().getContent());
        setComponentListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_view_vehicle);
        tvBrand = (TextView) findViewById(R.id.tv_view_vehicle_brand);
        tvModel = (TextView) findViewById(R.id.tv_view_vehicle_model);
        tvOdometer = (TextView) findViewById(R.id.tv_view_vehicle_odometer);
        tvManufactureDate = (TextView) findViewById(R.id.tv_view_vehicle_manufacture_date);
        tvHorsePower = (TextView) findViewById(R.id.tv_view_vehicle_horse_power);
        tvCubicCentimeters = (TextView) findViewById(R.id.tv_view_vehicle_cubic_centimeters);
        tvRegistrationPlate = (TextView) findViewById(R.id.tv_view_vehicle_registration_plate);
        tvVinPlate = (TextView) findViewById(R.id.tv_view_vehicle_vin_plate);
        tvNotes = (TextView) findViewById(R.id.tv_view_vehicle_notes);
    }

    @Override
    protected void setComponentListeners() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_vehicle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewVehicleActivity.class);
                intent.putExtra(RealmTable.ID, vehicleId);
                startActivity(intent);
            }
        });
    }
}
