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
            } else {
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
        } else {
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
        } else {
            showMessage("Expense updated!");
            Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
            intent.putExtra(Constants.ID, vehicleId);
            intent.putExtra(Constants.ITEM_ID, itemId);
            intent.putExtra(Constants.TYPE, Constants.ActivityType.EXPENSE.ordinal());
            startActivity(intent);
        }
        finish();
    }
}