package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Expense;
import stoyanov.valentin.mycar.realm.models.ExpenseType;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class NewExpenseActivity extends BaseActivity {

    private Realm myRealm;
    private TextInputLayout tilDate, tilTime, tilOdometer, tilPrice, tilNotes, tilType;
    private String vehicleId;
    private long vehicleOdometer;
    private RealmResults<ExpenseType> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        initComponents();
        setComponentListeners();
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
            if (isInputValid()) {
                myRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Expense expense = realm.createObject(Expense.class, UUID.randomUUID().toString());
                        String expenseTypeValue = tilType.getEditText().getText().toString();
                        ExpenseType type = realm.where(ExpenseType.class)
                                .equalTo(RealmTable.NAME, expenseTypeValue).findFirst();
                        if (type == null) {
                            type = realm.createObject(ExpenseType.class, UUID.randomUUID().toString());
                            type.setName(expenseTypeValue);
                        }
                        expense.setType(type);
                        Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                        note.setContent(tilNotes.getEditText().getText().toString());
                        expense.setNote(note);
                        Action action = realm.createObject(Action.class,
                                UUID.randomUUID().toString());
                        try {
                            Calendar calendar = Calendar.getInstance();
                            Date date = DateUtils.stringToDate(tilDate.getEditText().getText().toString());
                            calendar.setTime(date);
                            Date time = DateUtils.stringToTime(tilTime.getEditText().getText().toString());
                            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                            calendar.set(Calendar.MINUTE, time.getMinutes());
                            action.setDate(calendar.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long odometer = Long.parseLong(tilOdometer.getEditText()
                                .getText().toString());
                        action.setOdometer(odometer);
                        long price = MoneyUtils.stringToLong(tilOdometer.getEditText()
                                .getText().toString());
                        action.setPrice(price);
                        expense.setAction(action);
                        Vehicle vehicle = realm.where(Vehicle.class).equalTo(RealmTable.ID, vehicleId).findFirst();
                        vehicle.getExpenses().add(expense);
                        vehicle.setOdometer(odometer);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        showMessage("New expense saved!");
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        showMessage("Something went wrong...");
                        error.printStackTrace();
                    }
                });
                finish();
            }
            return true;
        }else if(id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isInputValid() {
        boolean valid = true;
        if (Long.parseLong(tilOdometer.getEditText().getText().toString()) < vehicleOdometer) {
            valid = false;
            tilOdometer.setError("The value is smaller than expected");
        }
        if (tilPrice.getEditText().getText().toString().length() < 1) {
            valid = false;
            tilPrice.setError("No price entered");
        }
        if (tilType.getEditText().getText().toString().length() < 1) {
            valid = false;
            showMessage("No service type entered");
        }
        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myRealm != null) {
            myRealm.close();
        }
    }

    @Override
    protected void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tilDate = (TextInputLayout) findViewById(R.id.til_new_expense_date);
        tilTime = (TextInputLayout) findViewById(R.id.til_new_expense_time);
        Calendar calendar = Calendar.getInstance();
        tilDate.getEditText().setText(DateUtils.dateToString(calendar.getTime()));
        tilTime.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
        tilOdometer = (TextInputLayout) findViewById(R.id.til_new_expense_odometer);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_expense_price);
        tilNotes = (TextInputLayout) findViewById(R.id.til_new_expense_notes);
        tilType = (TextInputLayout) findViewById(R.id.til_new_expense_type);
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
        vehicleOdometer = intent.getLongExtra(RealmTable.ODOMETER, 0);
        myRealm = Realm.getDefaultInstance();
        results = myRealm.where(ExpenseType.class).findAll();
        TextView tvLastOdometer = (TextView) findViewById(R.id.tv_new_expense_last_odometer);
        String text = String.format(getString(R.string.last_odometer_placeholder), vehicleOdometer);
        tvLastOdometer.setText(text);
    }

    @Override
    protected void setComponentListeners() {
        tilDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                tilDate.getEditText()
                                        .setText(DateUtils.dateToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMaxDate(calendar.getTime().getTime());
                datePickerDialog.show();
            }
        });
        tilTime.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewExpenseActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                tilTime.getEditText().setText(DateUtils.timeToString(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }
}
