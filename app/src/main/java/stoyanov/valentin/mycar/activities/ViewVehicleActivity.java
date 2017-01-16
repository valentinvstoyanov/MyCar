package stoyanov.valentin.mycar.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;

import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.ImageViewUtils;

public class ViewVehicleActivity extends BaseActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private ImageView imageView;
    private TextView tvBrand, tvModel, tvOdometer, tvManufactureDate;
    private TextView tvHorsePower, tvCubicCentimeters, tvRegistrationPlate;
    private TextView tvVinPlate, tvNotes;
    private String vehicleId;
    private LinearLayout llFuelTanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        initComponents();
        setContent();
        setComponentListeners();
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public void setContent() {
        Realm myRealm = Realm.getDefaultInstance();
        final Vehicle vehicle = myRealm.where(Vehicle.class)
                .equalTo(RealmTable.ID, vehicleId)
                .findFirst();
        myRealm.close();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_vehicle);
        int textIconsColor = vehicle.getColor().getTextIconsColor();
        toolbar.setTitle(vehicle.getName());
        toolbar.setTitleTextColor(textIconsColor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                verticalOffset = Math.abs(verticalOffset);
                int difference = appBarLayout.getTotalScrollRange() - toolbar.getHeight();
                if (verticalOffset >= difference) {
                    setStatusBarColor(ResourcesCompat.getColor(getResources()
                            , R.color.colorPrimaryDark, null));
                } else {
                    setStatusBarColor(vehicle.getColor().getRelevantDarkColor());
                }
            }
        });
        appBarLayout.setBackgroundColor(vehicle.getColor().getColor());
        setStatusBarColor(vehicle.getColor().getRelevantDarkColor());
        imageView.setBackground(ImageViewUtils.getDrawableByVehicleType(vehicle.getType().getName(),
                getApplicationContext(), vehicle.getColor().getTextIconsColor()));
        tvBrand.setText(vehicle.getBrand().getName());
        tvModel.setText(vehicle.getModel().getName());
        tvOdometer.setText(String.valueOf(vehicle.getOdometer()));
        tvManufactureDate.setText(DateUtils.dateToString(vehicle.getManufactureDate()));
        tvHorsePower.setText(String.valueOf(vehicle.getHorsePower()));
        tvCubicCentimeters.setText(String.valueOf(vehicle.getCubicCentimeter()));
        tvRegistrationPlate.setText(vehicle.getRegistrationPlate());
        tvVinPlate.setText(vehicle.getVinPlate());
        for (FuelTank fuelTank : vehicle.getFuelTanks()) {
            displayFuelTanks(fuelTank);
        }
        tvNotes.setText(vehicle.getNote().getContent());
    }

    private void displayFuelTanks(FuelTank fuelTank) {
        View view = getLayoutInflater().inflate(R.layout.row_view_fuel_tank, llFuelTanks, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgv_row_view_ft_fuel_type);
        TextView tvFuelType = (TextView) view.findViewById(R.id.tv_row_view_ft_fuel_type);
        TextView tvCapacity = (TextView) view.findViewById(R.id.tv_row_view_ft_capacity);
        TextView tvConsumption = (TextView) view.findViewById(R.id.tv_row_view_ft_consumption);
        imageView.setBackground(ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_menu_refueling, null));
        String text = String.format(getString(R.string.fuel_type_placeholder),
                fuelTank.getFuelType().getName());
        tvFuelType.setText(text);
        text = String.format(getString(R.string.capacity_placeholder), fuelTank.getCapacity());
        tvCapacity.setText(text);
        text = String.format(getString(R.string.consumption_placeholder), fuelTank.getConsumption());
        tvConsumption.setText(text);
        llFuelTanks.addView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }else {
            //add icon
            if (false) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    showMessage("Device does not support Bluetooth");
                }else {
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }

                    Intent discoverableIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(
                            BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
                    startActivity(discoverableIntent);

                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                    ArrayList<String> bluetoothDeviceNames = new ArrayList<>(pairedDevices.size());
                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            bluetoothDeviceNames.add(deviceName);
                            //String deviceHardwareAddress = device.getAddress(); // MAC address
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ENABLE_BT) {
                showMessage("Bluetooth enabled");
            }else if (requestCode == 180) {
                showMessage("Device discoverable for 180 seconds");
            }
        }
    }

    @Override
    public void initComponents() {
        imageView = (ImageView) findViewById(R.id.imgv_view_vehicle_type);
        tvBrand = (TextView) findViewById(R.id.tv_view_vehicle_brand);
        tvModel = (TextView) findViewById(R.id.tv_view_vehicle_model);
        tvOdometer = (TextView) findViewById(R.id.tv_view_vehicle_odometer);
        tvManufactureDate = (TextView) findViewById(R.id.tv_view_vehicle_manufacture_date);
        tvHorsePower = (TextView) findViewById(R.id.tv_view_vehicle_horse_power);
        tvCubicCentimeters = (TextView) findViewById(R.id.tv_view_vehicle_cubic_centimeters);
        tvRegistrationPlate = (TextView) findViewById(R.id.tv_view_vehicle_registration_plate);
        tvVinPlate = (TextView) findViewById(R.id.tv_view_vehicle_vin_plate);
        tvNotes = (TextView) findViewById(R.id.tv_view_vehicle_notes);
        llFuelTanks = (LinearLayout) findViewById(R.id.ll_view_vehicle_fuel_tanks);
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
    }

    @Override
    public void setComponentListeners() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view_vehicle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewVehicleActivity.class);
                intent.putExtra(RealmTable.ID, vehicleId);
                startActivity(intent);
                finish();
            }
        });
    }
}
