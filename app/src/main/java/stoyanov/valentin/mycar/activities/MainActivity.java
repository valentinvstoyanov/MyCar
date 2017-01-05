package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashSet;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.fragments.VehicleListFragment;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String FRAGMENT_TYPE = "fragment_type";
    private Spinner spnChooseVehicle;
    private Realm myRealm;
    private RealmResults<Vehicle> results;
    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> spinnerDataSet;
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

    private ArrayList<String> getVehicleNamesFromResults() {
        ArrayList<String> names = new ArrayList<>(results.size());
        for (Vehicle vehicle : results) {
            names.add(vehicle.getName());
        }
        return names;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        getSupportActionBar().setTitle(item.getTitle());
        int id = item.getItemId();
        /*if (id == R.id.nav_my_cars) {

        } else if (id == R.id.nav_services) {

        } else if (id == R.id.nav_expenses) {

        } else if (id == R.id.nav_refuelings) {

        } else if (id == R.id.nav_insurances) {

        } else if (id == R.id.nav_reminders) {

        } else if (id == R.id.nav_statistics) {

        } else if (id == R.id.nav_upcoming_events) {

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_about) {

        }*/
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_TYPE, id);
        bundle.putString(ViewVehicleActivity.VEHICLE_ID, spnChooseVehicle.getSelectedItem().toString());
        Fragment fragment = new VehicleListFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_content_main, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        spnChooseVehicle = (Spinner) findViewById(R.id.spn_main_choose_vehicle);
        myRealm = Realm.getDefaultInstance();
        results = myRealm.where(Vehicle.class)
                .findAllSortedAsync(RealmTable.NAME, Sort.ASCENDING);
        spinnerDataSet = getVehicleNamesFromResults();
        spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, spinnerDataSet);
        results.addChangeListener(callback);
    }

    @Override
    protected void setComponentListeners() {
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
                    String vehicleName = spnChooseVehicle.getSelectedItem().toString();
                    Vehicle vehicle = results.where()
                            .equalTo(RealmTable.NAME, vehicleName).findFirst();
                    intent.putExtra(ViewVehicleActivity.VEHICLE_ID, vehicle.getId());
                    intent.putExtra(NewServiceActivity.VEHICLE_ODOMETER, vehicle.getOdometer());
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_expense) {
                    intent = new Intent(getApplicationContext(), NewExpenseActivity.class);
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_reminder) {
                    intent = new Intent(getApplicationContext(), NewReminderActivity.class);
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_refueling) {
                    intent = new Intent(getApplicationContext(), NewRefuelingActivity.class);
                    startActivity(intent);
                    return true;
                } else if(menuItem.getItemId() == R.id.action_add_insurance) {
                    intent = new Intent(getApplicationContext(), NewInsuranceActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
}
