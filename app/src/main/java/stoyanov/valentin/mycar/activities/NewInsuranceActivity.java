package stoyanov.valentin.mycar.activities;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.NewBaseActivity;
import stoyanov.valentin.mycar.dialogs.NewCompanyDialog;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.models.DateNotification;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateTimePickerUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;
import stoyanov.valentin.mycar.utils.TextUtils;

public class NewInsuranceActivity extends NewBaseActivity {

    private ImageButton imgBtnAddCompany;
    private Spinner spnCompanies;
    private TextInputLayout tilTime, tilExpirationDate;
    private TextInputLayout tilExpirationTime, tilPrice;
    private String insuranceId;
    private RealmResults<Company> results;
    private ArrayAdapter<String> companiesAdapter;

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
    public void initComponents() {
        super.initComponents();
        tilDate.setHint("Date");
        tilTime = (TextInputLayout) findViewById(R.id.til_new_insurance_time);
        Calendar calendar = Calendar.getInstance();
        TextUtils.setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        tilExpirationDate = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_date);
        tilExpirationTime = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_time);
        calendar.add(Calendar.MINUTE, 5);
        TextUtils.setTextToTil(tilExpirationDate, DateUtils.dateToString(calendar.getTime()));
        TextUtils.setTextToTil(tilExpirationTime, DateUtils.timeToString(calendar.getTime()));
        spnCompanies = (Spinner) findViewById(R.id.spn_new_insurance_company_name);
        imgBtnAddCompany = (ImageButton) findViewById(R.id.img_btn_add_company);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_insurance_price);
        insuranceId = getIntent().getStringExtra(RealmTable.INSURANCES + RealmTable.ID);
        results = myRealm.where(Company.class).findAllSorted(RealmTable.NAME, Sort.ASCENDING);
        companiesAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, getCompanyNames());
        companiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCompanies.setAdapter(companiesAdapter);
        if (insuranceId != null) {
            setUpdate(true);
            setContent();
        }
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_insurance_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
        tilExpirationDate.setHint(getString(R.string.expiration_date));
        tilExpirationTime.setHint(getString(R.string.expiration_time));
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        DateTimePickerUtils.addDatePickerListener(NewInsuranceActivity.this, tilExpirationDate,
                new Date(), DateTimePickerUtils.PickerLimits.MIN);
        DateTimePickerUtils.addTimePickerListener(NewInsuranceActivity.this, tilTime);
        DateTimePickerUtils.addTimePickerListener(NewInsuranceActivity.this, tilExpirationTime);
        imgBtnAddCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NewCompanyDialog dialog = new NewCompanyDialog();
                dialog.setListener(new NewCompanyDialog.OnAddNewCompanyListener() {
                    @Override
                    public void onAddCompany(String companyName) {
                        dialog.dismiss();
                        results = myRealm.where(Company.class).findAllSorted(RealmTable.NAME, Sort.ASCENDING);
                        ArrayList<String> spinnerDataSet = getCompanyNames();
                        int index = spinnerDataSet.indexOf(companyName);
                        companiesAdapter.clear();
                        companiesAdapter.addAll(spinnerDataSet);
                        companiesAdapter.notifyDataSetChanged();
                        spnCompanies.setSelection(index);
                    }
                });
                dialog.show(getSupportFragmentManager(), "NewCompany");
            }
        });
    }

    @Override
    public void setContent() {
        Insurance insurance = myRealm.where(Insurance.class)
                .equalTo(RealmTable.ID, insuranceId).findFirst();
        TextUtils.setTextToTil(tilDate, DateUtils.dateToString(insurance.getDate()));
        TextUtils.setTextToTil(tilTime, DateUtils.timeToString(insurance.getDate()));
        TextUtils.setTextToTil(tilExpirationDate, DateUtils
                .dateToString(insurance.getNotification().getDate()));
        TextUtils.setTextToTil(tilExpirationTime, DateUtils
                .timeToString(insurance.getNotification().getDate()));
        TextUtils.setTextToTil(tilOdometer, String.valueOf(insurance.getOdometer()));
        tilOdometer.setEnabled(false);
        TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(insurance.getPrice())));
        TextUtils.setTextToTil(tilNote, insurance.getNote());
        spnCompanies.setSelection(results.indexOf(insurance.getCompany()));
    }

    @Override
    public boolean isInputValid() {
        boolean result = super.isInputValid();
        boolean valid = true;
        if (DateUtils.isDateInFuture(TextUtils.getTextFromTil(tilDate),
                TextUtils.getTextFromTil(tilTime))) {
            valid = false;
            tilDate.setError("Date&Time should be before the current");
        }
       /* if (DateUtils.isDateInPast(DateUtils.stringToDatetime(
                getTextFromTil(tilExpirationDate), getTextFromTil(tilExpirationTime)))) {
            valid = false;
            tilExpirationDate.setError("Insurance can't expire in the past");
        }*/
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilPrice))) {
            valid = false;
            tilPrice.setError("Price should be number");
        }
        return result && valid;
    }

    @Override
    public void saveToRealm() {
        final String companyId = results.get(spnCompanies.getSelectedItemPosition()).getId();
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Insurance insurance;
                DateNotification notification;
                if (isUpdate()) {
                    insurance = realm.where(Insurance.class).equalTo(RealmTable.ID, insuranceId)
                            .findFirst();
                    notification = insurance.getNotification();

                }else {
                    insuranceId = UUID.randomUUID().toString();
                    insurance = realm.createObject(Insurance.class, insuranceId);
                    notification = realm.createObject(DateNotification.class,
                            UUID.randomUUID().toString());
                    int id;
                    Number number = realm.where(DateNotification.class)
                            .max(RealmTable.NOTIFICATION_ID);
                    if (number == null) {
                        id = 0;
                    }else {
                        id = number.intValue() + 1;
                    }
                    notification.setNotificationId(id);
                }
                notification.setDate(DateUtils.stringToDatetime(TextUtils
                        .getTextFromTil(tilExpirationDate),
                                TextUtils.getTextFromTil(tilExpirationTime)));
                notification.setTriggered(false);
                insurance.setNotification(notification);

                Company company = realm.where(Company.class).equalTo(RealmTable.ID, companyId).findFirst();
                insurance.setCompany(company);

                /*Note note = realm.createObject(Note.class, UUID.randomUUID().toString());
                note.setContent(TextUtils.getTextFromTil(tilNote));*/
                insurance.setNote(TextUtils.getTextFromTil(tilNote));

                //Action action = realm.createObject(Action.class, UUID.randomUUID().toString());
                //Calendar calendar = Calendar.getInstance();
                Date date = DateUtils.stringToDate(TextUtils.getTextFromTil(tilDate));
                //calendar.setTime(date);
                Date time = DateUtils.stringToTime(TextUtils.getTextFromTil(tilTime));
                insurance.setDate(DateUtils.dateTime(date, time));
                //calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
                //calendar.set(Calendar.MINUTE, time.getMinutes());
                //action.setDate(calendar.getTime());
                long odometer = Long.parseLong(TextUtils.getTextFromTil(tilOdometer));
                insurance.setOdometer(odometer);
                //action.setOdometer(odometer);
                long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
                insurance.setPrice(price);
                //action.setPrice(price);
                //insurance.setAction(action);


                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(RealmTable.ID, getVehicleId())
                        .findFirst();
                if (odometer > getVehicleOdometer()) {
                    setVehicleOdometer(odometer);
                    vehicle.setOdometer(odometer);
                }
                vehicle.getInsurances().add(insurance);
                /*if (!isUpdate()) {
                    Vehicle vehicle = realm.where(Vehicle.class)
                            .equalTo(RealmTable.ID, getVehicleId())
                            .findFirst();
                    vehicle.setOdometer(odometer);
                    vehicle.getInsurances().add(insurance);
                }*/
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Insurance insurance = myRealm.where(Insurance.class)
                        .equalTo(RealmTable.ID, insuranceId).findFirst();


                Date notificationDate = insurance.getNotification().getDate();
                //Calendar calendar = Calendar.getInstance();
                //calendar.setTime(notificationDate);

                Notification notification = NotificationUtils.createNotification(getApplicationContext(),
                        getVehicleId(), RealmTable.INSURANCES + RealmTable.ID, insurance.getId(),
                        ActivityType.INSURANCE, ViewActivity.class, "Insurance",
                        insurance.getCompany().getName() + " insurance is expiring on " +
                                DateUtils.datetimeToString(insurance.getNotification().getDate()),
                        R.drawable.ic_insurance_black);

                NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                        insurance.getNotification().getNotificationId(), notificationDate.getTime());

                if (isUpdate()) {
                    showMessage("Insurance updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(RealmTable.ID, getVehicleId());
                    intent.putExtra(RealmTable.INSURANCES + RealmTable.ID, insuranceId);
                    intent.putExtra(RealmTable.TYPE, ActivityType.INSURANCE.ordinal());
                    startActivity(intent);
                }else {
                    showMessage("New insurance saved!");
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

    private ArrayList<String> getCompanyNames() {
        ArrayList<String> names = new ArrayList<>(results.size());
        for (Company company : results) {
            names.add(company.getName());
        }
        return names;
    }
}
