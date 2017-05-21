package stoyanov.valentin.mycar.activities;

import android.annotation.SuppressLint;
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
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.RealmSettings;
import stoyanov.valentin.mycar.realm.models.Refueling;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.RealmUtils;

public class ViewActivity extends BaseActivity {

    private String id;
    private Constants.ActivityType type;
    private Class aClass;
    private long vehicleOdometer;
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
        final int menuId = item.getItemId();

        if(menuId == android.R.id.home) {
            onBackPressed();
            return true;
        }else if (menuId == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
            builder.setTitle(getToolbarTitle());
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
                                            Insurance insurance = realm.where(Insurance.class)
                                                    .equalTo(Constants.ID, id)
                                                    .findFirst();
                                            RealmUtils.deleteProperty(insurance, type);
                                            break;
                                        case EXPENSE:
                                            Expense expense = realm.where(Expense.class)
                                                    .equalTo(Constants.ID, id)
                                                    .findFirst();
                                            RealmUtils.deleteProperty(expense, type);
                                            break;
                                        case SERVICE:
                                            Service service = realm.where(Service.class)
                                                    .equalTo(Constants.ID, id)
                                                    .findFirst();
                                            RealmUtils.deleteProperty(service, type);
                                            break;
                                        case REFUELING:
                                            Refueling refueling = realm.where(Refueling.class)
                                                    .equalTo(Constants.ID, id)
                                                    .findFirst();
                                            RealmUtils.deleteProperty(refueling, type);
                                            break;
                                    }
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    showMessage("Deleted!");
                                    finish();
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                    showMessage("Something went wrong...");
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
        setBackNavigation();
        type = Constants.ActivityType.values()[getIntent().getIntExtra(Constants.TYPE, 0)];
        vehicleId = getIntent().getStringExtra(Constants.ID);
    }

    @Override
    public void setComponentListeners() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_view);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), aClass);
                intent.putExtra(Constants.ID, vehicleId);
                intent.putExtra(Constants.ITEM_ID, id);
                intent.putExtra(Constants.ODOMETER, vehicleOdometer);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void setContent() {
        id = getIntent().getStringExtra(Constants.ITEM_ID);
        Realm myRealm = Realm.getDefaultInstance();
        Date date;
        long price;
        String note;
        RealmSettings settings = myRealm.where(RealmSettings.class).findFirst();
        switch (type) {
            case INSURANCE:
                final Insurance insurance = myRealm.where(Insurance.class)
                        .equalTo(Constants.ID, id).findFirst();
                setToolbarTitle(insurance.getCompany().getName());
                date = insurance.getDate();
                displayView(getString(R.string.date), DateUtils.dateToString(date));
                displayView(getString(R.string.expiration_date),
                        DateUtils.dateToString(insurance.getNotification().getDate()));
                displayView(getString(R.string.company_name), insurance.getCompany().getName());
                vehicleOdometer = insurance.getOdometer();
                price = insurance.getPrice();
                note = insurance.getNote();
                aClass = NewInsuranceActivity.class;
                break;
            case EXPENSE:
                Expense expense = myRealm.where(Expense.class)
                        .equalTo(Constants.ID, id).findFirst();
                setToolbarTitle(expense.getType());
                date = expense.getDate();
                vehicleOdometer = expense.getOdometer();
                price = expense.getPrice();
                note = expense.getNote();
                aClass = NewExpenseActivity.class;
                break;
            case REFUELING:
                Refueling refueling = myRealm.where(Refueling.class)
                        .equalTo(Constants.ID, id).findFirst();
                FuelTank fuelTank = refueling.getFuelTank();
                setToolbarTitle(fuelTank.getType());
                date = refueling.getDate();
                vehicleOdometer = refueling.getOdometer();
                price = refueling.getPrice();
                displayView(getString(R.string.quantity), String.valueOf(refueling.getQuantity()) + fuelTank.getUnit());
                displayView(getString(R.string.fuel_price),
                        MoneyUtils.longToString(new BigDecimal(refueling.getFuelPrice())));
                note = refueling.getNote();
                aClass = NewRefuelingActivity.class;
                break;
            default:
                Service service = myRealm.where(Service.class)
                        .equalTo(Constants.ID, id).findFirst();
                setToolbarTitle(service.getType().getName());
                if (service.shouldNotify()) {
                    if (service.getDateNotification() == null) {
                        displayView("Target odometer",
                                String.valueOf(service.getTargetOdometer() + settings.getLengthUnit()));
                    }else {
                        displayView("Notification date",
                                DateUtils.dateToString(service.getDateNotification().getDate()));
                    }
                }
                date = service.getDate();
                vehicleOdometer = service.getOdometer();
                price = service.getPrice();
                note = service.getNote();
                aClass = NewServiceActivity.class;
                break;
        }
        if (type != Constants.ActivityType.INSURANCE) {
            displayView(getString(R.string.date), DateUtils.datetimeToString(date));
        }
        displayView(getString(R.string.odometer), String.valueOf(vehicleOdometer) + settings.getLengthUnit());
        displayView(getString(R.string.price), MoneyUtils.longToString(new BigDecimal(price)) + settings.getCurrencyUnit());
        displayView(getString(R.string.notes), note);
        vehicleOdometer = myRealm.where(Vehicle.class)
                .equalTo(Constants.ID, vehicleId)
                .findFirst()
                .getOdometer();
        myRealm.close();
    }

    @SuppressLint("InflateParams")
    private void displayView(String title, String value) {
        View view = getLayoutInflater().inflate(R.layout.item_view_activity, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_item_view_title);
        TextView tvValue = (TextView) view.findViewById(R.id.tv_item_view_value);
        tvTitle.setText(title);
        tvValue.setText(value);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.content_view);
        linearLayout.addView(view);
    }
}
