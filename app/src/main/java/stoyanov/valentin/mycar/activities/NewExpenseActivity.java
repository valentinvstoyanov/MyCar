package stoyanov.valentin.mycar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.NewBaseActivity;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateTimePickerUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.TextUtils;

public class NewExpenseActivity extends NewBaseActivity {

    private Spinner spnType;
    private TextInputLayout tilTime, tilPrice;
    private String expenseId;
    private ArrayList<String> expenseTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        initComponents();
        setComponentListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (isInputValid()) {
                saveToRealm();
            }
            return true;
        }else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initComponents() {
        super.initComponents();
        tilDate.setHint("Date");
        tilTime = (TextInputLayout) findViewById(R.id.til_new_expense_time);
        Calendar calendar = Calendar.getInstance();
        TextUtils.setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_expense_price);
        spnType = (Spinner) findViewById(R.id.spn_new_expense_type);
        expenseId = getIntent().getStringExtra(RealmTable.EXPENSES + RealmTable.ID);
        if (expenseId != null) {
            setUpdate(true);
        }
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_expense_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
        expenseTypes = getExpenseTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, expenseTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
        if (isUpdate()) {
            setContent();
        }
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        DateTimePickerUtils.addTimePickerListener(NewExpenseActivity.this, tilTime);
    }

    @Override
    public void setContent() {
        Expense expense = myRealm.where(Expense.class)
                .equalTo(RealmTable.ID, expenseId)
                .findFirst();
        spnType.setSelection(expenseTypes.indexOf(expense.getType()));
        TextUtils.setTextToTil(tilDate, DateUtils.dateToString(expense.getDate()));
        TextUtils.setTextToTil(tilTime, DateUtils.timeToString(expense.getDate()));
        TextUtils.setTextToTil(tilOdometer, String.valueOf(expense.getOdometer()));
        tilOdometer.setEnabled(false);
        TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(expense.getPrice())));
        TextUtils.setTextToTil(tilNote, expense.getNote());
    }

    @Override
    public boolean isInputValid() {
        boolean result = super.isInputValid();
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
        return result && valid;
    }

    @Override
    public void saveToRealm() {
        /*if (getInterstitialAd().isLoaded()) {
            showMessage("ad");
            getInterstitialAd().show();
        }else {
            showMessage("not ad");
        }*/
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Expense expense;
                if (isUpdate()) {
                    expense = realm.where(Expense.class).equalTo(RealmTable.ID, expenseId)
                            .findFirst();
//                    expense.getNote().deleteFromRealm();
//                    expense.getAction().deleteFromRealm();
                }else {
                    expense = realm.createObject(Expense.class, UUID.randomUUID().toString());
                }
                /*ExpenseType type = realm.where(ExpenseType.class)
                        .equalTo(RealmTable.NAME, spnType.getSelectedItem().toString())
                        .findFirst();*/
                expense.setType(spnType.getSelectedItem().toString());

                /*Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                note.setContent(TextUtils.getTextFromTil(tilNote));*/
                expense.setNote(TextUtils.getTextFromTil(tilNote));

                //Action action = realm.createObject(Action.class, UUID.randomUUID().toString());
                //Calendar calendar = Calendar.getInstance();
                Date date = DateUtils.stringToDate(TextUtils.getTextFromTil(tilDate));
                //calendar.setTime(date);
                Date time = DateUtils.stringToTime(TextUtils.getTextFromTil(tilTime));
                expense.setDate(DateUtils.dateTime(date, time));
               // calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                //calendar.set(Calendar.MINUTE, time.getMinutes());
               // action.setDate(calendar.getTime());
                long odometer = Long.parseLong(TextUtils.getTextFromTil(tilOdometer));
                expense.setOdometer(odometer);
                //action.setOdometer(odometer);
                long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
                expense.setPrice(price);
               // action.setPrice(price);
                //expense.setAction(action);

               // if (!isUpdate()) {
                    Vehicle vehicle = realm.where(Vehicle.class)
                            .equalTo(RealmTable.ID, getVehicleId())
                            .findFirst();
                if (odometer > getVehicleOdometer()) {
                    setVehicleOdometer(odometer);
                    vehicle.setOdometer(odometer);
                }
                    vehicle.getExpenses().add(expense);
                //}
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isUpdate()) {
                    showMessage("Expense updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(RealmTable.ID, getVehicleId());
                    intent.putExtra(RealmTable.EXPENSES + RealmTable.ID, expenseId);
                    intent.putExtra(RealmTable.TYPE, ActivityType.EXPENSE.ordinal());
                    startActivity(intent);
                }else {
                    showMessage("New expense saved!");
                }
                //listener.onChange(getVehicleOdometer());
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
    }

    private ArrayList<String> getExpenseTypes() {
        String[] expenseTypes = getResources().getStringArray(R.array.expense_types);
        return new ArrayList<>(Arrays.asList(expenseTypes));
    }
}
