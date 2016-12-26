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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.adapters.ViewVehicleRecyclerviewAdapter;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IVehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleRepository;
import stoyanov.valentin.mycar.realm.repositories.impl.VehicleTypeRepository;
import stoyanov.valentin.mycar.utils.DateUtils;

public class ViewVehicleActivity extends BaseActivity {

    public static final String CAR_NAME = "car_name";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        initComponents();
        Intent intent = getIntent();
        String vehicleId = intent.getStringExtra(CAR_NAME);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_view_vehicle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VehicleRepository vehicleRepository = new VehicleRepository();
        vehicleRepository.getVehicleById(vehicleId, new IVehicleRepository.OnGetSigleVehicleCallback() {
            @Override
            public void onSuccess(Vehicle vehicle) {
                AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.setBackgroundColor(vehicle.getColor());
                toolbar.setTitle(vehicle.getName());
                String[] titles = getResources().getStringArray(R.array.vehicle_info);
                String[] values = new String[titles.length];
                values[0] = vehicle.getBrand().getName();
                values[1] = vehicle.getModel().getName();
                values[2] = String.valueOf(vehicle.getOdometer());
                values[3] = String.valueOf(vehicle.getHorsePower());
                values[4] = String.valueOf(vehicle.getCubicCentimeter());
                values[5] = DateUtils.manufactureDateToString(vehicle.getManufactureDate());
                values[6] = vehicle.getRegistrationPlate();
                values[7] = vehicle.getVinPlate();
                values[8] = "";//notes
                ViewVehicleRecyclerviewAdapter adapter =
                        new ViewVehicleRecyclerviewAdapter(titles, values);
                recyclerView.setAdapter(adapter);
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
