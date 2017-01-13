package stoyanov.valentin.mycar.activities;

import android.app.Notification;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.widget.TextView;
import org.apache.commons.lang3.math.NumberUtils;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;

public class NewInsuranceActivity extends NewBaseActivity {

    private TextInputLayout tilTime, tilExpirationDate;
    private TextInputLayout tilExpirationTime, tilPrice;
    private String insuranceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_insurance);
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
        tilTime = (TextInputLayout) findViewById(R.id.til_new_insurance_time);
        Calendar calendar = Calendar.getInstance();
        setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        tilExpirationDate = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_date);
        tilExpirationTime = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_time);
        calendar.add(Calendar.MINUTE, 5);
        setTextToTil(tilExpirationDate, DateUtils.dateToString(calendar.getTime()));
        setTextToTil(tilExpirationTime, DateUtils.timeToString(calendar.getTime()));
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_insurance_price);
        insuranceId = getIntent().getStringExtra(RealmTable.INSURANCES + RealmTable.ID);
        if (insuranceId != null) {
            setUpdate(true);
            setContent();
        }
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_insurance_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
    }

    @Override
    protected void setComponentListeners() {
        super.setComponentListeners();
        addDatePickerListener(tilExpirationDate);
        addTimePickerListener(tilTime);
        addTimePickerListener(tilExpirationTime);
    }

    @Override
    protected void setContent() {
        Insurance insurance = myRealm.where(Insurance.class)
                .equalTo(RealmTable.ID, insuranceId).findFirst();
        setTextToTil(tilDate, DateUtils.dateToString(insurance.getAction().getDate()));
        setTextToTil(tilTime, DateUtils.timeToString(insurance.getAction().getDate()));
        setTextToTil(tilExpirationDate, DateUtils.dateToString(insurance.getExpirationDate()));
        setTextToTil(tilExpirationTime, DateUtils.timeToString(insurance.getExpirationDate()));
        setTextToTil(tilOdometer, String.valueOf(insurance.getAction().getOdometer()));
        tilOdometer.setEnabled(false);
        setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(insurance.getAction()
                .getPrice())));
        setTextToTil(tilNote, insurance.getNote().getContent());
    }

    @Override
    protected boolean isInputValid() {
        boolean result = super.isInputValid();
        boolean valid = true;
        if (DateUtils.isDateInFuture(getTextFromTil(tilDate), getTextFromTil(tilTime))) {
            valid = false;
            tilDate.setError("Date&Time should be before the current");
        }
       /* if (DateUtils.isDateInPast(DateUtils.stringToDatetime(
                getTextFromTil(tilExpirationDate), getTextFromTil(tilExpirationTime)))) {
            valid = false;
            tilExpirationDate.setError("Insurance can't expire in the past");
        }*/
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
                Insurance insurance;
                if (isUpdate()) {
                    insurance = realm.where(Insurance.class).equalTo(RealmTable.ID, insuranceId)
                            .findFirst();
                }else {
                    insurance = realm.createObject(Insurance.class, UUID.randomUUID().toString());
                }

                Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                note.setContent(getTextFromTil(tilNote));
                insurance.setNote(note);

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
                insurance.setAction(action);

                insurance.setExpirationDate(DateUtils.stringToDatetime(getTextFromTil(tilExpirationDate),
                        getTextFromTil(tilExpirationTime)));

                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, getVehicleId())
                        .findFirst();
                vehicle.getInsurances().add(insurance);
                if (!isUpdate()) {
                    vehicle.setOdometer(odometer);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Notification notification = newNotification("Insurance",
                        "You should renew your insurance", R.drawable.ic_insurance_black);
                Date notificationDate = DateUtils.stringToDatetime(
                        getTextFromTil(tilExpirationDate),
                        getTextFromTil(tilExpirationTime));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(notificationDate);
                addNotification(notification, calendar.getTimeInMillis());
                if (isUpdate()) {
                    showMessage("Insurance updated!");
                }else {
                    showMessage("New insurance saved!");
                }
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
}
