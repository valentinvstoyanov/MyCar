package stoyanov.valentin.mycar.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.fragments.ListFragment;
import stoyanov.valentin.mycar.fragments.StatisticsFragment;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String STATISTIC_TYPE = "statistic_type";
    private static final int ENABLE_BLUETOOTH_REQUEST = 2;

    private Spinner spnChooseVehicle;
    private Realm myRealm;
    private RealmResults<Vehicle> results;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> spinnerDataSet;
    private DrawerLayout drawer;
    private int menuId;
    private RealmChangeListener<RealmResults<Vehicle>> callback =
            new RealmChangeListener<RealmResults<Vehicle>>() {
                @Override
                public void onChange(RealmResults<Vehicle> element) {
                    spinnerDataSet = getVehicleNamesFromResults();
                    spinnerAdapter.clear();
                    spinnerAdapter.addAll(spinnerDataSet);
                    spinnerAdapter.notifyDataSetChanged();

                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnChooseVehicle.setAdapter(spinnerAdapter);
        setComponentListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_settings, menu);
        return true;
    }

    private SmoothBluetooth smoothBluetooth;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        }else if (id == R.id.action_import) {
            SmoothBluetooth.Listener listener = new SmoothBluetooth.Listener() {
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
                public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {

                }

                @Override
                public void onDataReceived(int data) {
                    Log.d("Data: ", "d: " + data);
                }
            };
            smoothBluetooth = new SmoothBluetooth(getApplicationContext(),
                    SmoothBluetooth.ConnectionTo.ANDROID_DEVICE, SmoothBluetooth.Connection.SECURE,
                    listener);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        setToolbarTitle(item.getTitle().toString());
        int id = item.getItemId();
        menuId = id;
        if(results != null && !results.isEmpty()) {
            if (id == R.id.nav_my_cars) {
                spnChooseVehicle.setVisibility(View.INVISIBLE);
                openFragment(id);
            }else {
                if (id == R.id.nav_statistics) {
                    String vehicleId = results.get(spnChooseVehicle.getSelectedItemPosition()).getId();
                    if (results != null && !results.isEmpty()) {
                        Bundle bundle = new Bundle();
                        bundle.putString(RealmTable.ID, vehicleId);
                        bundle.putInt(STATISTIC_TYPE, Actions.SERVICE.ordinal());
                        StatisticsFragment fragment = new StatisticsFragment();
                        fragment.setArguments(bundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fl_content_main, fragment)
                                .commit();
                    }
                } else {
                    spnChooseVehicle.setVisibility(View.VISIBLE);
                    openFragment(id);
                }
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        results.removeChangeListener(callback);
        myRealm.close();
    }

    @Override
    public void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        spnChooseVehicle = (Spinner) findViewById(R.id.spn_main_choose_vehicle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        myRealm = Realm.getDefaultInstance();
        results = myRealm.where(Vehicle.class)
                .findAllSortedAsync(RealmTable.NAME, Sort.ASCENDING);
        spinnerDataSet = getVehicleNamesFromResults();
        spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, spinnerDataSet);
        results.addChangeListener(callback);
        spnChooseVehicle.setSelection(0);
        navigationView.getMenu().performIdentifierAction(R.id.nav_services, 0);
    }

    @Override
    public void setComponentListeners() {
        spnChooseVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                Intent intent;
                if(menuItem.getItemId() == R.id.action_add_car) {
                    intent = new Intent(getApplicationContext(), NewVehicleActivity.class);
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_service) {
                    intent = new Intent(getApplicationContext(), NewServiceActivity.class);
                    Vehicle vehicle = results.get(spnChooseVehicle.getSelectedItemPosition());
                    intent.putExtra(RealmTable.ID, vehicle.getId());
                    intent.putExtra(RealmTable.ODOMETER, vehicle.getOdometer());
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_expense) {
                    intent = new Intent(getApplicationContext(), NewExpenseActivity.class);
                    Vehicle vehicle = results.get(spnChooseVehicle.getSelectedItemPosition());
                    intent.putExtra(RealmTable.ID, vehicle.getId());
                    intent.putExtra(RealmTable.ODOMETER, vehicle.getOdometer());
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_reminder) {
                    intent = new Intent(getApplicationContext(), NewReminderActivity.class);
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_refueling) {
                    intent = new Intent(getApplicationContext(), NewRefuelingActivity.class);
                    Vehicle vehicle = results.get(spnChooseVehicle.getSelectedItemPosition());
                    intent.putExtra(RealmTable.ID, vehicle.getId());
                    intent.putExtra(RealmTable.ODOMETER, vehicle.getOdometer());
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_insurance) {
                    intent = new Intent(getApplicationContext(), NewInsuranceActivity.class);
                    Vehicle vehicle = results.get(spnChooseVehicle.getSelectedItemPosition());
                    intent.putExtra(RealmTable.ID, vehicle.getId());
                    intent.putExtra(RealmTable.ODOMETER, vehicle.getOdometer());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        spnChooseVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                openFragment(menuId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void setContent() {

    }

    private void openFragment(int id) {
        String vehicleId = results.get(spnChooseVehicle.getSelectedItemPosition()).getId();
        if (results != null && !results.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putInt(FRAGMENT_TYPE, id);
            bundle.putString(RealmTable.ID, vehicleId);
            Fragment fragment = new ListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_content_main, fragment)
                    .commit();
        }
    }

    private ArrayList<String> getVehicleNamesFromResults() {
        ArrayList<String> names = new ArrayList<>(results.size());
        for (Vehicle vehicle : results) {
            names.add(vehicle.getName());
        }
        return names;
    }

    public enum Actions {
        SERVICE, INSURANCE, EXPENSE, REFUELING
    }
}
