package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ToggleButton;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.NewBaseActivity;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.DateNotification;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.ServiceType;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.DateTimePickerUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;
import stoyanov.valentin.mycar.utils.RealmUtils;
import stoyanov.valentin.mycar.utils.TextUtils;

public class NewServiceActivity extends NewBaseActivity {

    private TextInputLayout tilType;
    private ToggleButton toggleButton;
    private Button btnNotificationDate;
    private TextInputLayout tilOdometerNotification;
    private RealmResults<ServiceType> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);
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
        tilType = (TextInputLayout) findViewById(R.id.til_new_service_type);
        toggleButton = (ToggleButton) findViewById(R.id.toggle_btn_new_service);
        toggleButton.setChecked(true);
        tilOdometerNotification = (TextInputLayout) findViewById(R.id.til_odometer_notification);
        tilOdometerNotification.setVisibility(View.GONE);
        btnNotificationDate = (Button) findViewById(R.id.btn_new_service_notification_date);
        results = myRealm.where(ServiceType.class).findAll();
        AutoCompleteTextView actvType = (AutoCompleteTextView) findViewById(R.id.actv_new_service_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line,
                getServiceTypeNamesFromResults());
        actvType.setAdapter(adapter);
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        btnNotificationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerUtils.showDatePicker(NewServiceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = DateUtils.getDateFromInts(year, month, dayOfMonth);
                        btnNotificationDate.setText(date);
                    }
                });
            }
        });
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tilOdometerNotification.animate()
                            .translationX(tilOdometerNotification.getWidth())
                            .setDuration(200);
                    tilOdometerNotification.setVisibility(View.GONE);
                    btnNotificationDate.animate().translationX(0).setDuration(200);
                    btnNotificationDate.setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    btnNotificationDate.setText(DateUtils.dateToString(calendar.getTime()));
                }else {
                    btnNotificationDate.animate()
                            .translationX(btnNotificationDate.getWidth())
                            .setDuration(200);
                    btnNotificationDate.setVisibility(View.GONE);
                    tilOdometerNotification.animate().translationX(0).setDuration(200);
                    tilOdometerNotification.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void populateNewItem() {
        Calendar calendar = Calendar.getInstance();
        btnDate.setText(DateUtils.dateToString(calendar.getTime()));
        btnTime.setText(DateUtils.timeToString(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        btnNotificationDate.setText(DateUtils.dateToString(calendar.getTime()));
    }

    @Override
    protected void populateExistingItem() {
        Service service = myRealm.where(Service.class)
                .equalTo(Constants.ID, itemId)
                .findFirst();
        TextUtils.setTextToAutoComplete(tilType, service.getType().getName());
        btnDate.setText(DateUtils.dateToString(service.getDate()));
        btnTime.setText(DateUtils.timeToString(service.getDate()));
        TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(service.getPrice())));
        TextUtils.setTextToTil(tilOdometer, String.valueOf(service.getOdometer()));
        TextUtils.setTextToTil(tilNote, service.getNote());
        if (service.shouldNotify()) {
            if (service.getDateNotification() == null) {
                toggleButton.setChecked(false);
                TextUtils.setTextToTil(tilOdometerNotification, String.valueOf(service.getTargetOdometer()));
            } else {
                toggleButton.setChecked(true);
                DateNotification dateNotification = service.getDateNotification();
                btnNotificationDate.setText(DateUtils.dateToString(dateNotification.getDate()));
            }
        }
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;

        String type = TextUtils.getTextFromAutoComplete(tilType);
        if (type.length() > 36) {
            valid = false;
            tilType.setError("Type is too long");
        }else {
            if (!StringUtils.isAlphanumericSpace(type)) {
                valid = false;
                tilType.setError("Only letters allowed here");
            } else if (type.isEmpty()) {
                valid = false;
                tilType.setError("This field can't be empty");
            }
        }

        if (toggleButton.isChecked()) {
            if (DateUtils.isNotValidDate(btnNotificationDate.getText().toString(), false)) {
                valid = false;
                showMessage("Invalid notification date");
            }else if (!DateUtils.isExpirationDateValid(btnNotificationDate.getText().toString())) {
                valid = false;
                showMessage("Notification day must be at least one day later");
            }
        }else {
            if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilOdometerNotification))) {
                valid = false;
                tilOdometerNotification.setError("Numeric value expected");
            }else {
                if (NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometerNotification)) < getVehicleOdometer()) {
                    valid = false;
                    tilOdometerNotification.setError("Target odometer must be bigger than current");
                }
            }
        }

        return super.isInputValid() && valid;
    }

    @Override
    protected void saveItem(Realm realm) {
        final boolean isChecked = toggleButton.isChecked();
        Vehicle vehicle = realm.where(Vehicle.class)
                .equalTo(Constants.ID, vehicleId)
                .findFirst();
        Service service = new Service();
        if (isNewItem()) {
            service.setId(UUID.randomUUID().toString());
        }else {
            Service oldService = realm.where(Service.class)
                    .equalTo(Constants.ID, itemId)
                    .findFirst();
            RealmUtils.deleteProperty(oldService, Constants.ActivityType.SERVICE);
            service.setId(itemId);
        }

        String typeText = TextUtils.getTextFromAutoComplete(tilType);
        ServiceType type = realm.where(ServiceType.class)
                .equalTo(Constants.NAME, typeText)
                .findFirst();
        if (type == null) {
            type = realm.createObject(ServiceType.class, UUID.randomUUID().toString());
            type.setName(typeText);
        }
        service.setType(type);

        Date date = DateUtils.stringToDate(btnDate.getText().toString());
        service.setDate(date);

        long odometer = Long.parseLong(TextUtils.getTextFromTil(tilOdometer));
        service.setOdometer(odometer);
        if (odometer > getVehicleOdometer()) {
            vehicle.setOdometer(odometer);
            setVehicleOdometer(odometer);
        }

        long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
        service.setPrice(price);

        service.setShouldNotify(true);
        if (isChecked) {
            DateNotification notification = realm.createObject(DateNotification.class,
                    UUID.randomUUID().toString());
            notification.setTriggered(false);
            date = DateUtils.stringToDate(btnNotificationDate.getText().toString());
            notification.setDate(date);

            int notificationId;
            Number number = realm.where(DateNotification.class)
                    .max(Constants.NOTIFICATION_ID);
            if (number == null) {
                notificationId = 0;
            }else {
                notificationId = number.intValue() + 1;
            }
            notification.setNotificationId(notificationId);
            service.setDateNotification(notification);
            setNotification(service);
        }else {
            service.setTargetOdometer(NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometerNotification)));
            service.setOdometerTriggered(false);
        }
        service.setNote(TextUtils.getTextFromTil(tilNote));
        vehicle.getServices().add(realm.copyToRealmOrUpdate(service));
    }

    @Override
    protected void onItemSaved() {
        if (isNewItem()) {
            showMessage("New service saved!");
        }else {
            showMessage("Service updated!");
            Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
            intent.putExtra(Constants.ID, vehicleId);
            intent.putExtra(Constants.SERVICES + Constants.ID, itemId);
            intent.putExtra(Constants.TYPE, Constants.ActivityType.SERVICE.ordinal());
            startActivity(intent);
        }

        finish();
    }

    private void setNotification(Service service) {
        Date notificationDate = service.getDateNotification().getDate();

        Notification notification = NotificationUtils.createNotification
                (
                        this, vehicleId, service.getId(), Constants.ActivityType.SERVICE,
                        "Service", service.getType().getName() + " should be revised at " + DateUtils.datetimeToString(service.getDateNotification().getDate()),
                        R.drawable.ic_services_black
                );

     /*   Notification notification = NotificationUtils.createNotification
                (
                        getApplicationContext(), vehicleId,
                        Constants.SERVICES + Constants.ID, service.getId(),
                        Constants.ActivityType.SERVICE, ViewActivity.class,
                        "Service", service.getType().getName() +
                                " should be revised at " + DateUtils.datetimeToString
                                (
                                        service.getDateNotification().getDate()
                                ), R.drawable.ic_services_black
                );*/

        NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                service.getDateNotification().getNotificationId(), notificationDate.getTime());
    }

    private ArrayList<String> getServiceTypeNamesFromResults() {
        ArrayList<String> arrayList = new ArrayList<>(results.size());
        for (ServiceType type : results) {
            arrayList.add(type.getName());
        }
        return arrayList;
    }
}
