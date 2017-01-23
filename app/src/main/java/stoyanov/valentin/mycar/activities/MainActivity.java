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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.fragments.InfoFragment;
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

    private BluetoothSPP bluetoothSPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        setComponentListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_settings, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == ENABLE_BLUETOOTH_REQUEST) {
                showMessage("Bluetooth enabled...");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_import) {
            bluetoothSPP = new BluetoothSPP(this);
            bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                @Override
                public void onDataReceived(byte[] data, String message) {
                    showMessage(message);
                }
            });
            if (!bluetoothSPP.isBluetoothEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, ENABLE_BLUETOOTH_REQUEST);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        setToolbarTitle(item.getTitle().toString());
        menuId = item.getItemId();
        openFragment();
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
        navigationView.getMenu().performIdentifierAction(R.id.nav_info, 0);
        navigationView.setCheckedItem(R.id.nav_info);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnChooseVehicle.setAdapter(spinnerAdapter);
    }

    @Override
    public void setComponentListeners() {
        spnChooseVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                openFragment();
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
                Class aClass;
                Vehicle vehicle = results.get(spnChooseVehicle.getSelectedItemPosition());
                String vehicleId = vehicle.getId();
                long vehicleOdometer = vehicle.getOdometer();
                switch (menuItem.getItemId()) {
                    case R.id.action_add_car:
                        aClass = NewVehicleActivity.class;
                        vehicleId = null;
                        break;
                    case R.id.action_add_service:
                        aClass = NewServiceActivity.class;
                        break;
                    case R.id.action_add_expense:
                        aClass = NewExpenseActivity.class;
                        break;
                    case R.id.action_add_refueling:
                        aClass = NewRefuelingActivity.class;
                        break;
                    case R.id.action_add_insurance:
                        aClass = NewInsuranceActivity.class;
                        break;
                    default:
                        return false;
                }
                intent = new Intent(getApplicationContext(), aClass);
                if (vehicleId != null) {
                    intent.putExtra(RealmTable.ID, vehicleId);
                    intent.putExtra(RealmTable.ODOMETER, vehicleOdometer);
                }
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void setContent() {}

   private void openFragment() {
        if (results != null && !results.isEmpty()) {
            String vehicleId = results.get(spnChooseVehicle.getSelectedItemPosition()).getId();
            Fragment fragment;
            Bundle bundle = new Bundle();
            bundle.putString(RealmTable.ID, vehicleId);
            if (menuId == R.id.nav_statistics) {
                fragment = new StatisticsFragment();
            }else if (menuId == R.id.nav_info) {
                fragment = new InfoFragment();
            }else {
                fragment = new ListFragment();
                bundle.putInt(FRAGMENT_TYPE, menuId);
            }
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
