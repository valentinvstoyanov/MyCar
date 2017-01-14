package stoyanov.valentin.mycar.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.commons.lang3.math.NumberUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.ServiceType;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.ValidationUtils;

public class NewServiceActivity extends NewBaseActivity {

    private TextInputLayout tilTime, tilPrice, tilType;
    private ToggleButton toggleButton;
    private LinearLayout llDateTimeNotification;
    private TextInputLayout tilOdometerNotification;
    private TextInputLayout tilNotificationDate;
    private TextInputLayout tilNotificationTime;
    private RealmResults<ServiceType> results;
    private String serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
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
        tilTime = (TextInputLayout) findViewById(R.id.til_new_service_time);
        Calendar calendar = Calendar.getInstance();
        setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_service_price);
        tilType = (TextInputLayout) findViewById(R.id.til_new_service_type);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_btn_new_service);
        toggleButton.setChecked(true);
        llDateTimeNotification = (LinearLayout) findViewById(R.id.ll_date_time_notification);
        tilOdometerNotification = (TextInputLayout) findViewById(R.id.til_odometer_notification);
        tilOdometerNotification.setVisibility(View.GONE);
        tilNotificationDate = (TextInputLayout) findViewById(R.id.til_new_service_notification_date);
        tilNotificationTime = (TextInputLayout) findViewById(R.id.til_new_service_notification_time);
        setTextToTil(tilNotificationDate, DateUtils.dateToString(calendar.getTime()));
        setTextToTil(tilNotificationTime, DateUtils.timeToString(calendar.getTime()));
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_service_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
        serviceId = getIntent().getStringExtra(RealmTable.SERVICES + RealmTable.ID);
        if(serviceId != null) {
            setUpdate(true);
        }
        results = myRealm.where(ServiceType.class).findAll();
        AutoCompleteTextView actvType = (AutoCompleteTextView) findViewById(R.id.actv_new_service_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                getServiceTypeNamesFromResults());
        actvType.setAdapter(adapter);
    }

    @Override
    protected void setComponentListeners() {
        super.setComponentListeners();
        addTimePickerListener(tilTime);
        addDatePickerListener(tilNotificationDate);
        addTimePickerListener(tilNotificationTime);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    tilOdometerNotification.animate()
                            .translationX(tilOdometerNotification.getWidth()).setDuration(200);
                    tilOdometerNotification.setVisibility(View.GONE);
                    llDateTimeNotification.animate().translationX(0).setDuration(200);
                    llDateTimeNotification.setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    setTextToTil(tilNotificationDate, DateUtils.dateToString(calendar.getTime()));
                    setTextToTil(tilNotificationTime, DateUtils.timeToString(calendar.getTime()));
                }else {
                    llDateTimeNotification.animate()
                            .translationX(llDateTimeNotification.getWidth()).setDuration(200);
                    llDateTimeNotification.setVisibility(View.GONE);
                    tilOdometerNotification.animate().translationX(0).setDuration(200);
                    tilOdometerNotification.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void setContent() {
        Service service = myRealm.where(Service.class)
                .equalTo(RealmTable.ID, serviceId)
                .findFirst();
        setTextToTil(tilType, service.getType().getName());
        setTextToTil(tilDate, DateUtils.dateToString(service.getAction().getDate()));
        setTextToTil(tilTime, DateUtils.timeToString(service.getAction().getDate()));
        setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(service.getAction().getPrice())));
        setTextToTil(tilOdometer, String.valueOf(service.getAction().getOdometer()));
        tilOdometer.setEnabled(false);
        setTextToTil(tilNote, service.getNote().getContent());
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
        /*if (ValidationUtils.isInputValid(getTextFromAutoComplete(tilType))) {
            valid = false;
            tilType.setError("Incorrect input");
        }*/
        return result && valid;
    }

    @Override
    protected void saveToRealm() {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Service service;
                if (isUpdate()) {
                    service = realm.where(Service.class)
                            .equalTo(RealmTable.ID, serviceId).findFirst();
                    service.getNote().deleteFromRealm();
                    service.getAction().deleteFromRealm();
                }else {
                    service = realm.createObject(Service.class, UUID.randomUUID().toString());
                }

                String serviceTypeValue = getTextFromAutoComplete(tilType);
                ServiceType type = realm.where(ServiceType.class)
                        .equalTo(RealmTable.NAME, serviceTypeValue).findFirst();
                if (type == null) {
                    type = realm.createObject(ServiceType.class, UUID.randomUUID().toString());
                    type.setName(serviceTypeValue);
                }
                service.setType(type);

                Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                note.setContent(getTextFromTil(tilNote));
                service.setNote(note);

                Action action = realm.createObject(Action.class, UUID.randomUUID().toString());
                Calendar calendar = Calendar.getInstance();
                Date date = DateUtils.stringToDate(getTextFromTil(tilDate));
                calendar.setTime(date);
                Date time = DateUtils.stringToTime(getTextFromTil(tilTime));
                calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                calendar.set(Calendar.MINUTE, time.getMinutes());
                action.setDate(calendar.getTime());
                long odometer = Long.parseLong(tilOdometer.getEditText()
                        .getText().toString());
                action.setOdometer(odometer);
                long price = MoneyUtils.stringToLong(tilOdometer.getEditText()
                        .getText().toString());
                action.setPrice(price);
                service.setAction(action);

                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, getVehicleId())
                        .findFirst();
                vehicle.getServices().add(service);
                if (!isUpdate()) {
                    vehicle.setOdometer(odometer);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isUpdate()) {
                    showMessage("Service updated!");
                }else {
                    showMessage("New service saved!");
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

    private ArrayList<String> getServiceTypeNamesFromResults() {
        ArrayList<String> arrayList = new ArrayList<>(results.size());
        for (ServiceType type : results) {
            arrayList.add(type.getName());
        }
        return arrayList;
    }
}
