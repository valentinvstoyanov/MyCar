package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.NewBaseActivity;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.TextUtils;

public class NewExpenseActivity extends NewBaseActivity {

    private Spinner spnType;
    private ArrayList<String> expenseTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_new_expense);
        super.onCreate(savedInstanceState);
        initComponents();
        setContent();
        setComponentListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setEnabled(false);
        int id = item.getItemId();
        if (id == R.id.action_save) {
            progressBar.setIndeterminate(true);
            if (isInputValid()) {
                saveToRealm();
            }else {
                item.setEnabled(true);
                progressBar.setIndeterminate(false);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initComponents() {
        super.initComponents();
        spnType = (Spinner) findViewById(R.id.spn_new_expense_type);
        expenseTypes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.expense_types)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.textview_spinner, expenseTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
    }

    @Override
    protected void populateNewItem() {
        Calendar calendar = Calendar.getInstance();
        btnDate.setText(DateUtils.dateToString(calendar.getTime()));
        btnTime.setText(DateUtils.timeToString(calendar.getTime()));
    }

    @Override
    protected void populateExistingItem() {
        Expense expense = myRealm.where(Expense.class)
                .equalTo(Constants.ID, itemId)
                .findFirst();
        spnType.setSelection(expenseTypes.indexOf(expense.getType()));
        btnDate.setText(DateUtils.dateToString(expense.getDate()));
        btnTime.setText(DateUtils.timeToString(expense.getDate()));
        TextUtils.setTextToTil(tilOdometer, String.valueOf(expense.getOdometer()));
        TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(expense.getPrice())));
        TextUtils.setTextToTil(tilNote, expense.getNote());
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;

        if (DateUtils.isDateInFuture(btnDate.getText().toString(), btnTime.getText().toString())) {
            valid = false;
            showMessage("The date is in the future");
        }

        return super.isInputValid() && valid;
    }

    @Override
    protected void saveItem(Realm realm) {
        Vehicle vehicle = realm.where(Vehicle.class)
                .equalTo(Constants.ID, vehicleId)
                .findFirst();
        Expense expense = new Expense();

        if (isNewItem()) {
            expense.setId(UUID.randomUUID().toString());
        }else {
            vehicle.getExpenses()
                    .where()
                    .equalTo(Constants.ID, itemId)
                    .findFirst()
                    .deleteFromRealm();
            expense.setId(itemId);
        }

        expense.setType(spnType.getSelectedItem().toString());

        Date date = DateUtils.stringToDate(btnDate.getText().toString());
        Date time = DateUtils.stringToTime(btnTime.getText().toString());
        expense.setDate(DateUtils.dateTime(date, time));

        long odometer = NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer));
        expense.setOdometer(odometer);
        if (odometer > getVehicleOdometer()) {
            vehicle.setOdometer(odometer);
            setVehicleOdometer(odometer);
        }

        long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
        expense.setPrice(price);

        expense.setNote(TextUtils.getTextFromTil(tilNote));
        vehicle.getExpenses().add(realm.copyToRealmOrUpdate(expense));
    }

    @Override
    protected void onItemSaved() {
        if (isNewItem()) {
            showMessage("New expense saved!");
        }else {
            showMessage("Expense updated!");
            Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
            intent.putExtra(Constants.ID, vehicleId);
            intent.putExtra(Constants.ITEM_ID, itemId);
            intent.putExtra(Constants.TYPE, Constants.ActivityType.EXPENSE.ordinal());
            startActivity(intent);
        }
        finish();
    }

    /*    @Override
    public void saveToRealm() {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(Constants.ID, getVehicleId())
                        .findFirst();
                Expense expense = new Expense();

                if (isUpdate()) {
                    vehicle.getExpenses()
                            .where()
                            .equalTo(Constants.ID, expenseId)
                            .findFirst()
                            .deleteFromRealm();
                    expense.setId(expenseId);
                }else {
                    expense.setId(UUID.randomUUID().toString());
                }

                expense.setType(spnType.getSelectedItem().toString());

                Date date = DateUtils.stringToDate(btnDate.getText().toString());
                Date time = DateUtils.stringToTime(btnTime.getText().toString());
                expense.setDate(DateUtils.dateTime(date, time));

                long odometer = NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer));
                expense.setOdometer(odometer);
                if (odometer > getVehicleOdometer()) {
                    vehicle.setOdometer(odometer);
                    setVehicleOdometer(odometer);
                }

                long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
                expense.setPrice(price);

                expense.setNote(TextUtils.getTextFromTil(tilNote));
                vehicle.getExpenses().add(realm.copyToRealmOrUpdate(expense));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isUpdate()) {
                    showMessage("Expense updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(Constants.ID, getVehicleId());
                    intent.putExtra(Constants.EXPENSES + Constants.ID, expenseId);
                    intent.putExtra(Constants.TYPE, ActivityType.EXPENSE.ordinal());
                    startActivity(intent);
                }else {
                    showMessage("New expense saved!");
                }
                odometerChanged(getVehicleOdometer());
                finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                showMessage("Something went wrong...");
                error.printStackTrace();
                finish();
            }
        });
    }*/

 /*   private Spinner spnType;
    private TextInputLayout tilTime, tilPrice;
    private String expenseId;
    private ArrayList<String> expenseTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        initComponents();
        setContent();
        setComponentListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setEnabled(false);
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (isInputValid()) {
                *//*if (getInterstitialAd().isLoaded()) {
                    showMessage("ad");
                    getInterstitialAd().show();
                }else {
                    showMessage("not ad");
                }*//*
                saveToRealm();
            }else {
                item.setEnabled(true);
            }
            return true;
        }*//*else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }*//*
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initComponents() {
        super.initComponents();
        expenseId = getIntent().getStringExtra(Constants.EXPENSES + Constants.ID);
        if (expenseId != null) {
            setUpdate(true);
        }
        tilTime = (TextInputLayout) findViewById(R.id.til_new_expense_time);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_expense_price);
        spnType = (Spinner) findViewById(R.id.spn_new_expense_type);
        expenseTypes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.expense_types)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, expenseTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        DateTimePickerUtils.addTimePickerListener(NewExpenseActivity.this, tilTime);
    }

    @Override
    public void setContent() {
        tilDate.setHint("Date");
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_expense_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
        if (isUpdate()) {
            Expense expense = myRealm.where(Expense.class)
                    .equalTo(Constants.ID, expenseId)
                    .findFirst();
            spnType.setSelection(expenseTypes.indexOf(expense.getType()));
            TextUtils.setTextToTil(tilDate, DateUtils.dateToString(expense.getDate()));
            TextUtils.setTextToTil(tilTime, DateUtils.timeToString(expense.getDate()));
            TextUtils.setTextToTil(tilOdometer, String.valueOf(expense.getOdometer()));
            TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(expense.getPrice())));
            TextUtils.setTextToTil(tilNote, expense.getNote());
        }else {
            Calendar calendar = Calendar.getInstance();
            TextUtils.setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        }
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;
        if (DateUtils.isDateInFuture(TextUtils.getTextFromTil(tilDate),
                TextUtils.getTextFromTil(tilTime))) {
            valid = false;
            tilDate.setError("The date is in the future");
        }
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilPrice))) {
            valid = false;
            tilPrice.setError("Price should be number");
        }
        return super.isInputValid() && valid;
    }

    @Override
    public void saveToRealm() {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(Constants.ID, getVehicleId())
                        .findFirst();
                Expense expense = new Expense();

                if (isUpdate()) {
                    vehicle.getExpenses()
                            .where()
                            .equalTo(Constants.ID, expenseId)
                            .findFirst()
                            .deleteFromRealm();
                    expense.setId(expenseId);
                }else {
                    expense.setId(UUID.randomUUID().toString());
                }

                expense.setType(spnType.getSelectedItem().toString());

                Date date = DateUtils.stringToDate(TextUtils.getTextFromTil(tilDate));
                Date time = DateUtils.stringToTime(TextUtils.getTextFromTil(tilTime));
                expense.setDate(DateUtils.dateTime(date, time));

                long odometer = NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer));
                expense.setOdometer(odometer);
                if (odometer > getVehicleOdometer()) {
                    vehicle.setOdometer(odometer);
                    setVehicleOdometer(odometer);
                }

                long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
                expense.setPrice(price);

                expense.setNote(TextUtils.getTextFromTil(tilNote));
                vehicle.getExpenses().add(realm.copyToRealmOrUpdate(expense));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isUpdate()) {
                    showMessage("Expense updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(Constants.ID, getVehicleId());
                    intent.putExtra(Constants.EXPENSES + Constants.ID, expenseId);
                    intent.putExtra(Constants.TYPE, ActivityType.EXPENSE.ordinal());
                    startActivity(intent);
                }else {
                    showMessage("New expense saved!");
                }
                odometerChanged(getVehicleOdometer());
                finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                showMessage("Something went wrong...");
                error.printStackTrace();
                finish();
            }
        });
    }*/
}
