package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class ViewActivity extends BaseActivity {

    private String id;
    private ViewType type;
    private Class aClass;
    private long odometer;
    private String typeStr;
    private String vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        initComponents();
        setContent();
        setComponentListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = ViewType.values()[getIntent().getIntExtra(RealmTable.TYPE, 0)];
        vehicleId = getIntent().getStringExtra(RealmTable.ID);
    }

    @Override
    public void setComponentListeners() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), aClass);
                intent.putExtra(RealmTable.ID, vehicleId);
                intent.putExtra(typeStr + RealmTable.ID, id);
                intent.putExtra(RealmTable.ODOMETER, odometer);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void setContent() {
        Intent intent = getIntent();
        Realm myRealm = Realm.getDefaultInstance();
        switch (type) {
            case INSURANCE:
                typeStr = RealmTable.INSURANCES;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                final Insurance insurance = myRealm.where(Insurance.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                setToolbarTitle(insurance.getCompany().getName());
                displayView(getString(R.string.date),
                        DateUtils.datetimeToString(insurance.getAction().getDate()));
                displayView(getString(R.string.expiration_date),
                        DateUtils.datetimeToString(insurance.getExpirationDate()));
                displayView(getString(R.string.company_name), insurance.getCompany().getName());
                displayView(getString(R.string.odometer),
                        String.valueOf(insurance.getAction().getOdometer()));
                displayView(getString(R.string.price),
                        MoneyUtils.longToString(new BigDecimal(insurance.getAction().getPrice())));
                displayView(getString(R.string.notes), insurance.getNote().getContent());
                aClass = NewInsuranceActivity.class;
                odometer = insurance.getAction().getOdometer();
                break;
            case EXPENSE:
                typeStr = RealmTable.EXPENSES;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                Expense expense = myRealm.where(Expense.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                setToolbarTitle(expense.getType().getName());
                displayView(getString(R.string.date),
                        DateUtils.datetimeToString(expense.getAction().getDate()));
                displayView(getString(R.string.odometer),
                        String.valueOf(expense.getAction().getOdometer()));
                displayView(getString(R.string.price),
                        MoneyUtils.longToString(new BigDecimal(expense.getAction().getPrice())));
                displayView(getString(R.string.notes), expense.getNote().getContent());
                aClass = NewExpenseActivity.class;
                odometer = expense.getAction().getOdometer();
                break;
            case REFUELING:
                typeStr = RealmTable.REFUELINGS;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                Refueling refueling = myRealm.where(Refueling.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                FuelTank fuelTank = myRealm.where(FuelTank.class)
                        .equalTo(RealmTable.ID, refueling.getFuelTankId()).findFirst();
                setToolbarTitle(fuelTank.getFuelType().getName());
                displayView(getString(R.string.date),
                        DateUtils.datetimeToString(refueling.getAction().getDate()));
                displayView(getString(R.string.odometer),
                        String.valueOf(refueling.getAction().getOdometer()));
                displayView(getString(R.string.price),
                        MoneyUtils.longToString(new BigDecimal(refueling.getAction().getPrice())));
                displayView(getString(R.string.quantity),
                        String.valueOf(refueling.getQuantity()) + fuelTank.getFuelType().getUnit());
                displayView(getString(R.string.fuel_price_placeholder),
                        MoneyUtils.longToString(new BigDecimal(refueling.getFuelPrice())));
                displayView(getString(R.string.notes), refueling.getNote().getContent());
                aClass = NewRefuelingActivity.class;
                odometer = refueling.getAction().getOdometer();
                typeStr = RealmTable.REFUELINGS;
                break;
            default:
                typeStr = RealmTable.SERVICES;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                Service service = myRealm.where(Service.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                setToolbarTitle(service.getType().getName());
                displayView(getString(R.string.date),
                        DateUtils.datetimeToString(service.getAction().getDate()));
                displayView(getString(R.string.odometer),
                        String.valueOf(service.getAction().getOdometer()));
                displayView(getString(R.string.price),
                        MoneyUtils.longToString(new BigDecimal(service.getAction().getPrice())));
                displayView(getString(R.string.notes), service.getNote().getContent());
                aClass = NewServiceActivity.class;
                odometer = service.getAction().getOdometer();
                break;
        }
        myRealm.close();
    }

    private void displayView(String title, String value) {
        View view = getLayoutInflater().inflate(R.layout.item_view_activity, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_view_title);
        TextView tvValue = (TextView) view.findViewById(R.id.tv_item_view_value);
        tvTitle.setText(title);
        tvValue.setText(value);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_view);
        linearLayout.addView(view);
    }

    public enum ViewType {
        INSURANCE, EXPENSE, REFUELING, SERVICE
    }
}
