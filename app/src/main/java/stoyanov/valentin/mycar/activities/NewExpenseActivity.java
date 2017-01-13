package stoyanov.valentin.mycar.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
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
import io.realm.RealmResults;
import io.realm.Sort;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.ExpenseType;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

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
    protected void initComponents() {
        super.initComponents();
        tilDate.setHint("Date");
        tilTime = (TextInputLayout) findViewById(R.id.til_new_expense_time);
        Calendar calendar = Calendar.getInstance();
        setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
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
    protected void setComponentListeners() {
        super.setComponentListeners();
        addTimePickerListener(tilTime);
    }

    @Override
    protected void setContent() {
        Expense expense = myRealm.where(Expense.class)
                .equalTo(RealmTable.ID, expenseId)
                .findFirst();
        spnType.setSelection(expenseTypes.indexOf(expense.getType().getName()));
        setTextToTil(tilDate, DateUtils.dateToString(expense.getAction().getDate()));
        setTextToTil(tilTime, DateUtils.timeToString(expense.getAction().getDate()));
        setTextToTil(tilOdometer, String.valueOf(expense.getAction().getOdometer()));
        tilOdometer.setEnabled(false);
        setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(expense.getAction()
                .getPrice())));
        setTextToTil(tilNote, expense.getNote().getContent());
    }

    @Override
    protected boolean isInputValid() {
        boolean result = super.isInputValid();
        boolean valid = true;
        if (DateUtils.isDateInFuture(getTextFromTil(tilDate), getTextFromTil(tilTime))) {
            valid = false;
            tilDate.setError("The date is in the future");
        }
        if (!NumberUtils.isCreatable(getTextFromTil(tilPrice))) {
            valid = false;
            tilPrice.setError("Price should be number");
        }
        return result && valid;
    }

    @Override
    protected void saveToRealm() {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Expense expense;
                if (isUpdate()) {
                    expense = realm.where(Expense.class).equalTo(RealmTable.ID, expenseId)
                            .findFirst();
                    expense.getNote().deleteFromRealm();
                    expense.getAction().deleteFromRealm();
                }else {
                    expense = realm.createObject(Expense.class, UUID.randomUUID().toString());
                }
                ExpenseType type = realm.where(ExpenseType.class)
                        .equalTo(RealmTable.NAME, spnType.getSelectedItem().toString())
                        .findFirst();
                expense.setType(type);

                Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                note.setContent(getTextFromTil(tilNote));
                expense.setNote(note);

                Action action = realm.createObject(Action.class, UUID.randomUUID().toString());
                Calendar calendar = Calendar.getInstance();
                Date date = DateUtils.stringToDate(getTextFromTil(tilDate));
                calendar.setTime(date);
                Date time = DateUtils.stringToTime(getTextFromTil(tilTime));
                calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                calendar.set(Calendar.MINUTE, time.getMinutes());
                action.setDate(calendar.getTime());
                long odometer = Long.parseLong(getTextFromTil(tilOdometer));
                action.setOdometer(odometer);
                long price = MoneyUtils.stringToLong(getTextFromTil(tilPrice));
                action.setPrice(price);
                expense.setAction(action);

                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, getVehicleId())
                        .findFirst();
                vehicle.getExpenses().add(expense);
                if (!isUpdate()) {
                    vehicle.setOdometer(odometer);
                }
                Log.d("asd " + expense.getType().getName(), "execute: ");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                showMessage("New expense saved!");
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
