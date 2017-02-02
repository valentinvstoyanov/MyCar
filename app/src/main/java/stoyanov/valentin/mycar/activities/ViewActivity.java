package stoyanov.valentin.mycar.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Date;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.BaseActivity;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.RealmSettings;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class ViewActivity extends BaseActivity {

    private String id;
    private ViewType type;
    private Class aClass;
    private long vehicleOdometer;
    private String typeStr;
    private String vehicleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        initComponents();
        setContent();
        setComponentListeners();

        /*ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Log.d("ads", "onCreate: ");
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-5561372550558397~2312873861");
            AdView mAdView = (AdView) findViewById(R.id.adView_view);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }else if (id == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
            builder.setTitle(getSupportActionBar().getTitle());
            builder.setMessage("Are you sure you want to delete it?");
            builder.setCancelable(true)
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Realm myRealm = Realm.getDefaultInstance();
                            myRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    switch (type) {
                                        case INSURANCE:
                                            realm.where(Insurance.class).equalTo(RealmTable.ID, id)
                                                    .findFirst().deleteFromRealm();
                                            break;
                                        case EXPENSE:
                                            realm.where(Expense.class).equalTo(RealmTable.ID, id)
                                                    .findFirst().deleteFromRealm();
                                            break;
                                        case SERVICE:
                                            realm.where(Service.class).equalTo(RealmTable.ID, id)
                                                    .findFirst().deleteFromRealm();
                                            break;
                                        case REFUELING:
                                            realm.where(Refueling.class).equalTo(RealmTable.ID, id)
                                                    .findFirst().deleteFromRealm();
                                            break;
                                    }
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    showMessage(aClass.getName() + " deleted!");
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
                intent.putExtra(RealmTable.ODOMETER, vehicleOdometer);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void setContent() {
        Intent intent = getIntent();
        Realm myRealm = Realm.getDefaultInstance();
        Date date;
        long price;
        String note;
        switch (type) {
            case INSURANCE:
                typeStr = RealmTable.INSURANCES;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                final Insurance insurance = myRealm.where(Insurance.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                setToolbarTitle(insurance.getCompany().getName());
                date = insurance.getAction().getDate();
                displayView(getString(R.string.expiration_date),
                        DateUtils.datetimeToString(insurance.getNotification().getNotificationDate()));
                displayView(getString(R.string.company_name), insurance.getCompany().getName());
                vehicleOdometer = insurance.getAction().getOdometer();
                price = insurance.getAction().getPrice();
                note = insurance.getNote().getContent();
                aClass = NewInsuranceActivity.class;
                break;
            case EXPENSE:
                typeStr = RealmTable.EXPENSES;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                Expense expense = myRealm.where(Expense.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                setToolbarTitle(expense.getType().getName());
                date = expense.getAction().getDate();
                vehicleOdometer = expense.getAction().getOdometer();
                price = expense.getAction().getPrice();
                note = expense.getNote().getContent();
                aClass = NewExpenseActivity.class;
                break;
            case REFUELING:
                typeStr = RealmTable.REFUELINGS;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                Refueling refueling = myRealm.where(Refueling.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                FuelTank fuelTank = myRealm.where(FuelTank.class)
                        .equalTo(RealmTable.ID, refueling.getFuelTankId()).findFirst();
                setToolbarTitle(fuelTank.getFuelType().getName());
                date = refueling.getAction().getDate();
                vehicleOdometer = refueling.getAction().getOdometer();
                price = refueling.getAction().getPrice();
                displayView(getString(R.string.quantity),
                        String.valueOf(refueling.getQuantity()) + fuelTank.getFuelType().getUnit());
                displayView(getString(R.string.fuel_price_placeholder),
                        MoneyUtils.longToString(new BigDecimal(refueling.getFuelPrice())));
                note = refueling.getNote().getContent();
                aClass = NewRefuelingActivity.class;
                typeStr = RealmTable.REFUELINGS;
                break;
            default:
                typeStr = RealmTable.SERVICES;
                id = intent.getStringExtra(typeStr + RealmTable.ID);
                Service service = myRealm.where(Service.class)
                        .equalTo(RealmTable.ID, id).findFirst();
                setToolbarTitle(service.getType().getName());
                date = service.getAction().getDate();
                vehicleOdometer = service.getAction().getOdometer();
                price = service.getAction().getPrice();
                note = service.getNote().getContent();
                displayView(getString(R.string.notes), service.getNote().getContent());
                aClass = NewServiceActivity.class;
                break;
        }
        RealmSettings settings = myRealm.where(RealmSettings.class).findFirst();
        displayView(getString(R.string.date), DateUtils.datetimeToString(date));
        displayView(getString(R.string.odometer), String.valueOf(vehicleOdometer) + settings.getLengthUnit());
        displayView(getString(R.string.price), MoneyUtils.longToString(new BigDecimal(price)) + settings.getCurrencyUnit());
        displayView(getString(R.string.notes), note);
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
