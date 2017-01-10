package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;
import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.adapters.ViewVehicleRecyclerViewAdapter;
import stoyanov.valentin.mycar.dialogs.ChangeVehiclePropertyDialog;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;

public class ViewVehicleActivity extends BaseActivity {

    private Toolbar toolbar;
    private Realm myRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);
        initComponents();
        Intent intent = getIntent();
        final String vehicleId = intent.getStringExtra(RealmTable.ID);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_view_vehicle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        myRealm = Realm.getDefaultInstance();
        final Vehicle vehicle = myRealm.where(Vehicle.class)
                                .equalTo(RealmTable.ID, vehicleId)
                                .findFirst();
        appBarLayout.setBackgroundColor(vehicle.getColor());
        toolbar.setTitle(vehicle.getName());
        ArrayList<String> titles = new ArrayList<>();
        titles.addAll(Arrays.asList(getResources().getStringArray(R.array.vehicle_info)));
        FuelTank[] fuelTanks = vehicle.getFuelTanks().toArray(new FuelTank[0]);
        for (int i = 0; i < fuelTanks.length; i++) {
            titles.add(titles.size() - 1, "ft" + i);
        }
        String[] values = new String[titles.size() - fuelTanks.length];
        values[0] = vehicle.getBrand().getName();
        values[1] = vehicle.getModel().getName();
        values[2] = String.valueOf(vehicle.getOdometer());
        values[3] = String.valueOf(vehicle.getHorsePower());
        values[4] = String.valueOf(vehicle.getCubicCentimeter());
        values[5] = DateUtils.manufactureDateToString(vehicle.getManufactureDate());
        values[6] = vehicle.getRegistrationPlate();
        values[7] = vehicle.getVinPlate();
        values[8] = vehicle.getNote().getContent();
        final ViewVehicleRecyclerViewAdapter adapter =
                new ViewVehicleRecyclerViewAdapter(getApplicationContext(),
                        titles, values, fuelTanks);
        adapter.setListener(new ViewVehicleRecyclerViewAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(final int position, String title) {
                if (position == 5) {
                    final Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ViewVehicleActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                    final String date = DateUtils.manufactureDateToString(calendar.getTime());
                                    myRealm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            try {
                                                vehicle.setManufactureDate(DateUtils.stringToDate(date));
                                                adapter.changeValue(5, date);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }, calendar.get(Calendar.YEAR)
                            , calendar.get(Calendar.MONTH)
                            , calendar.get(Calendar.DAY_OF_MONTH));
                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    datePicker.setMaxDate(calendar.getTime().getTime());
                    datePickerDialog.show();
                }else {
                    final ChangeVehiclePropertyDialog dialog = new ChangeVehiclePropertyDialog();
                    dialog.setText(title);
                    if (position >= 2 && position <= 4) {
                        dialog.setValidationType(ChangeVehiclePropertyDialog.ValidationType.NUMBER);
                    }else if (position == 8){
                        dialog.setValidationType(ChangeVehiclePropertyDialog.ValidationType.NOTE);
                    }else {
                        dialog.setValidationType(ChangeVehiclePropertyDialog.ValidationType.STRING);
                    }
                    dialog.setListener(new ChangeVehiclePropertyDialog.OnChangeVehiclePropertyListener() {
                        @Override
                        public void onChange(final String input) {
                            dialog.dismiss();
                            Realm.Transaction transaction;
                            switch (position) {
                                case 0:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Brand brand = realm.where(Brand.class)
                                                                .equalTo(RealmTable.NAME, input)
                                                                .findFirst();
                                            if (brand == null) {
                                                brand = realm.createObject(Brand.class, UUID.randomUUID().toString());
                                                brand.setName(input);
                                            }
                                            vehicle.setBrand(brand);
                                        }
                                    };
                                    break;
                                case 1:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Model model = realm.where(Model.class)
                                                    .equalTo(RealmTable.NAME, input)
                                                    .findFirst();
                                            if (model == null) {
                                                model = realm.createObject(Model.class, UUID.randomUUID().toString());
                                                model.setName(input);
                                            }
                                            vehicle.setModel(model);
                                        }
                                    };
                                    break;
                                case 2:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            vehicle.setOdometer(Long.parseLong(input));
                                        }
                                    };
                                    break;
                                case 3:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            vehicle.setHorsePower(Integer.parseInt(input));
                                        }
                                    };
                                    break;
                                case 4:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            vehicle.setCubicCentimeter(Integer.parseInt(input));
                                        }
                                    };
                                    break;
                                case 6:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            vehicle.setRegistrationPlate(input);
                                        }
                                    };
                                    break;
                                case 7:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            vehicle.setVinPlate(input);
                                        }
                                    };
                                    break;
                                default:
                                    transaction = new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.where(Note.class).equalTo(RealmTable.ID, vehicle.getNote().getId())
                                                    .findFirst().deleteFromRealm();
                                            Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                                            note.setContent(input);
                                            vehicle.setNote(note);
                                        }
                                    };
                                    break;
                            }
                            myRealm.executeTransaction(transaction);
                            adapter.changeValue(position, input);
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "dialogChangeVehicleProperty");
                }
            }
        });
        recyclerView.setAdapter(adapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
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
