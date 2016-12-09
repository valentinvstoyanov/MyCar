package stoyanov.valentin.mycar.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.fragments.MyListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
        if (id == R.id.nav_my_cars) {
            Fragment fragment = new MyListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_content_main, fragment).commit();
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
