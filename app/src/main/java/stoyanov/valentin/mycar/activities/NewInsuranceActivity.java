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
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.abstracts.NewBaseActivity;
import stoyanov.valentin.mycar.dialogs.NewCompanyDialog;
import stoyanov.valentin.mycar.realm.models.Action;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Note;
import stoyanov.valentin.mycar.realm.models.RealmNotification;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;

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
        setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
        tilExpirationDate = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_date);
        tilExpirationTime = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_time);
        calendar.add(Calendar.MINUTE, 5);
        setTextToTil(tilExpirationDate, DateUtils.dateToString(calendar.getTime()));
        setTextToTil(tilExpirationTime, DateUtils.timeToString(calendar.getTime()));
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
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        addDatePickerListener(tilExpirationDate);
        addTimePickerListener(tilTime);
        addTimePickerListener(tilExpirationTime);
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
        setTextToTil(tilDate, DateUtils.dateToString(insurance.getAction().getDate()));
        setTextToTil(tilTime, DateUtils.timeToString(insurance.getAction().getDate()));
        setTextToTil(tilExpirationDate, DateUtils
                .dateToString(insurance.getNotification().getNotificationDate()));
        setTextToTil(tilExpirationTime, DateUtils
                .dateToString(insurance.getNotification().getNotificationDate()));
        setTextToTil(tilOdometer, String.valueOf(insurance.getAction().getOdometer()));
        tilOdometer.setEnabled(false);
        setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(insurance.getAction()
                .getPrice())));
        setTextToTil(tilNote, insurance.getNote().getContent());
        spnCompanies.setSelection(results.indexOf(insurance.getCompany()));
    }

    @Override
    public boolean isInputValid() {
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
    public void saveToRealm() {
        final String companyId = results.get(spnCompanies.getSelectedItemPosition()).getId();
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Insurance insurance;
                RealmNotification notification;
                if (isUpdate()) {
                    insurance = realm.where(Insurance.class).equalTo(RealmTable.ID, insuranceId)
                            .findFirst();
                    notification = insurance.getNotification();

                }else {
                    insuranceId = UUID.randomUUID().toString();
                    insurance = realm.createObject(Insurance.class, insuranceId);
                    notification = realm.createObject(RealmNotification.class,
                            UUID.randomUUID().toString());
                    int id;
                    Number number = realm.where(RealmNotification.class)
                            .max(RealmTable.NOTIFICATION_ID);
                    if (number == null) {
                        id = 0;
                    }else {
                        id = number.intValue() + 1;
                    }
                    notification.setNotificationId(id);
                }
                notification.setNotificationDate(DateUtils.stringToDatetime(getTextFromTil(tilExpirationDate),
                        getTextFromTil(tilExpirationTime)));
                notification.setTriggered(false);
                insurance.setNotification(notification);

                Company company = realm.where(Company.class).equalTo(RealmTable.ID, companyId).findFirst();
                insurance.setCompany(company);

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

               // insurance.setExpirationDate(DateUtils.stringToDatetime(getTextFromTil(tilExpirationDate),
                 //       getTextFromTil(tilExpirationTime)));


                if (!isUpdate()) {
                    Vehicle vehicle = realm.where(Vehicle.class)
                            .equalTo(RealmTable.ID, getVehicleId())
                            .findFirst();
                    vehicle.setOdometer(odometer);
                    vehicle.getInsurances().add(insurance);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                /*Notification notification = newNotification("Insurance",
                        "You should renew your insurance", R.drawable.ic_insurance_black);
                Date notificationDate = DateUtils.stringToDatetime(
                        getTextFromTil(tilExpirationDate),
                        getTextFromTil(tilExpirationTime));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(notificationDate);
                addNotification(notification, calendar.getTimeInMillis());*/

                Insurance insurance = myRealm.where(Insurance.class)
                        .equalTo(RealmTable.ID, insuranceId).findFirst();


                Date notificationDate = insurance.getNotification().getNotificationDate();
                /*DateUtils.stringToDatetime(
                        getTextFromTil(tilExpirationDate),
                        getTextFromTil(tilExpirationTime));*/
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(notificationDate);

                Notification notification = NotificationUtils.createNotification(getApplicationContext(),
                        getVehicleId(), RealmTable.INSURANCES + RealmTable.ID, insurance.getId(),
                        ViewActivity.ViewType.INSURANCE, ViewActivity.class, "Expiring insurance",
                        insurance.getCompany().getName() + " insurance is expiring on " +
                                DateUtils.datetimeToString(insurance.getNotification().getNotificationDate()),
                        R.drawable.ic_insurance_black);

                NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                        insurance.getNotification().getNotificationId(), calendar.getTimeInMillis());

                if (isUpdate()) {
                    showMessage("Insurance updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(RealmTable.ID, getVehicleId());
                    intent.putExtra(RealmTable.INSURANCES + RealmTable.ID, insuranceId);
                    intent.putExtra(RealmTable.TYPE, ViewActivity.ViewType.INSURANCE.ordinal());
                    startActivity(intent);
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

    private ArrayList<String> getCompanyNames() {
        ArrayList<String> names = new ArrayList<>(results.size());
        for (Company company : results) {
            names.add(company.getName());
        }
        return names;
    }
}
