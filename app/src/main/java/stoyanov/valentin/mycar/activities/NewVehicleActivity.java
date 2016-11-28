package stoyanov.valentin.mycar.activities;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import es.dmoral.coloromatic.ColorOMaticDialog;
import es.dmoral.coloromatic.IndicatorMode;
import es.dmoral.coloromatic.OnColorSelectedListener;
import es.dmoral.coloromatic.colormode.ColorMode;
import stoyanov.valentin.mycar.R;

public class NewVehicleActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private View viewVehicleColor;
    private LinearLayout llSecondFuelTank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_new_vehicle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toggleButton = (ToggleButton) findViewById(R.id.tbtn_additional_fuel_tank);
        viewVehicleColor = findViewById(R.id.view_new_vehicle_color);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    llSecondFuelTank = (LinearLayout) findViewById(R.id.ll_new_vehicle_second_fuel_tank);
                    llSecondFuelTank.setVisibility(View.VISIBLE);
                } else {
                    llSecondFuelTank.setVisibility(View.INVISIBLE);
                    llSecondFuelTank = null;
                }
            }
        });

        viewVehicleColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorOMaticDialog.Builder()
                        .initialColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                        .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS
                        .indicatorMode(IndicatorMode.HEX) // HEX or DECIMAL; Note that using HSV with IndicatorMode.HEX is not recommended
                        .onColorSelected(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(@ColorInt int i) {
                                viewVehicleColor.setBackgroundColor(i);
                            }
                        })
                        .showColorIndicator(true) // Default false, choose to show text indicator showing the current color in HEX or DEC (see images) or not
                        .create()
                        .show(getSupportFragmentManager(), "Choose your vehicle color");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            Toast.makeText(this,"SAVE",Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
