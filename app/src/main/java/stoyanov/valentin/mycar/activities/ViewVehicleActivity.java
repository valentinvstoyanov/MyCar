package stoyanov.valentin.mycar.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;
import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.dialogs.BluetoothDevicesDialog;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.ImageViewUtils;

public class ViewVehicleActivity extends BaseActivity {

    private static final int ENABLE_BLUETOOTH_REQUEST = 1;
    private ImageView imageView;
    private TextView tvBrand, tvModel, tvOdometer, tvManufactureDate;
    private TextView tvHorsePower, tvCubicCentimeters, tvRegistrationPlate;
    private TextView tvVinPlate, tvNotes;
    private String vehicleId;
    private LinearLayout llFuelTanks;

    private SmoothBluetooth smoothBluetooth;
    private SmoothBluetooth.Listener listener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            showMessage("Bluetooth not supported");
        }

        @Override
        public void onBluetoothNotEnabled() {
            showMessage("Bluetooth is not enabled");
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, ENABLE_BLUETOOTH_REQUEST);
        }

        @Override
        public void onConnecting(Device device) {
            showMessage("Connecting to " + device.getName());
        }

        @Override
        public void onConnected(Device device) {
            showMessage("Connected to " + device.getName());
            Realm myRealm = Realm.getDefaultInstance();
            Vehicle vehicle = myRealm.copyFromRealm(myRealm.where(Vehicle.class)
            .equalTo(RealmTable.ID, vehicleId).findFirst());
            String data = new Gson().toJson(vehicle);
            smoothBluetooth.send(data);
        }

        @Override
        public void onDisconnected() {
            showMessage("Device disconnected");
        }

        @Override
        public void onConnectionFailed(Device device) {
            showMessage("Failed to connect to " + device.getName());
            if (device.isPaired()) {
                smoothBluetooth.doDiscovery();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            showMessage("Searching...");
        }

        @Override
        public void onDiscoveryFinished() {
            showMessage("Searching has finished");
        }

        @Override
        public void onNoDevicesFound() {
            showMessage("No devices found");
        }

        @Override
        public void onDevicesFound(final List<Device> deviceList, final SmoothBluetooth.ConnectionCallback connectionCallback) {
            final BluetoothDevicesDialog dialog = new BluetoothDevicesDialog();
            dialog.setDevices(getDevicesNames(deviceList));
            dialog.setListener(new BluetoothDevicesDialog.OnDevicePickedListener() {
                @Override
                public void onPicked(int position) {
                    connectionCallback.connectTo(deviceList.get(position));
                    dialog.dismiss();
                }
            });
            dialog.show(getSupportFragmentManager(), "bluetooth_dialog");
        }

        @Override
        public void onDataReceived(int data) {}
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ENABLE_BLUETOOTH_REQUEST) {
            if(resultCode == RESULT_OK) {
                smoothBluetooth.tryConnection();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        initComponents();
        setContent();
        setComponentListeners();
        smoothBluetooth = new SmoothBluetooth(getApplicationContext(), SmoothBluetooth.ConnectionTo.ANDROID_DEVICE,
                SmoothBluetooth.Connection.SECURE, listener);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_vehicle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }else if (id == R.id.action_export){
                smoothBluetooth.doDiscovery();
            return true;
        }else if (id == R.id.action_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewVehicleActivity.this);
            builder.setTitle(getSupportActionBar().getTitle());
            builder.setMessage("The following vehicle will be deleted. Are you sure?");
            builder.setCancelable(true)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Realm myRealm = Realm.getDefaultInstance();
                            myRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Vehicle vehicle = realm.where(Vehicle.class)
                                            .equalTo(RealmTable.ID, vehicleId)
                                            .findFirst();
                                    vehicle.getFuelTanks().deleteAllFromRealm();
                                    vehicle.getInsurances().deleteAllFromRealm();
                                    vehicle.getNote().deleteFromRealm();
                                    vehicle.getServices().deleteAllFromRealm();
                                    vehicle.getExpenses().deleteAllFromRealm();
                                    vehicle.getRefuelings().deleteAllFromRealm();
                                    vehicle.deleteFromRealm();
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    showMessage("Vehicle deleted!");
                                    finish();
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                    showMessage("Something went wrong...");
                                    finish();
                                }
                            });
                        }
                    });
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        smoothBluetooth.stop();
    }

    private String[] getDevicesNames(List<Device> deviceList) {
        String[] deviceNames = new String[deviceList.size()];
        int i = 0;
        for (Device device : deviceList) {
            deviceNames[i] = device.getName();
            i++;
        }
        return deviceNames;
    }
}
