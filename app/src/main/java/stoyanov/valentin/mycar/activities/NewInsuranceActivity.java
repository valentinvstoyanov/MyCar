package stoyanov.valentin.mycar.activities;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;

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
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Company;
import stoyanov.valentin.mycar.realm.models.DateNotification;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.DateTimePickerUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.MoneyUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;
import stoyanov.valentin.mycar.utils.RealmUtils;
import stoyanov.valentin.mycar.utils.TextUtils;

public class NewInsuranceActivity extends NewBaseActivity {
/*
    private ImageButton imgBtnAddCompany;*/
    private Spinner spnCompanies;
    private RealmResults<Company> results;
    private ArrayAdapter<String> companiesAdapter;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_insurance);
        initComponents();
        btnTime.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.ic_calendar_black_24dp), null, null);
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
                companyId = results.get(spnCompanies.getSelectedItemPosition()).getId();
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
        spnCompanies = (Spinner) findViewById(R.id.spn_new_insurance_company_name);
        results = myRealm.where(Company.class).findAllSorted(Constants.NAME, Sort.ASCENDING);
        companiesAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, getCompanyNames());
        companiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCompanies.setAdapter(companiesAdapter);
    }

    @Override
    public void setComponentListeners() {
        super.setComponentListeners();
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerUtils.showDatePicker(NewInsuranceActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = DateUtils.getDateFromInts(year, month, dayOfMonth);
                        btnTime.setText(date);
                    }
                });
            }
        });
        findViewById(R.id.img_btn_add_company).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NewCompanyDialog dialog = new NewCompanyDialog();
                dialog.setListener(new NewCompanyDialog.OnAddNewCompanyListener() {
                    @Override
                    public void onAddCompany(String companyName) {
                        dialog.dismiss();
                        results = myRealm.where(Company.class).findAllSorted(Constants.NAME, Sort.ASCENDING);
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
    protected void populateNewItem() {
        Calendar calendar = Calendar.getInstance();
        btnDate.setText(DateUtils.dateToString(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        btnTime.setText(DateUtils.dateToString(calendar.getTime()));
    }

    @Override
    protected void populateExistingItem() {
        Insurance insurance = myRealm.where(Insurance.class)
                .equalTo(Constants.ID, itemId)
                .findFirst();
        btnDate.setText(DateUtils.dateToString(insurance.getDate()));
        btnTime.setText(DateUtils.dateToString(insurance.getNotification().getDate()));
        TextUtils.setTextToTil(tilOdometer, String.valueOf(insurance.getOdometer()));
        TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(insurance.getPrice())));
        TextUtils.setTextToTil(tilNote, insurance.getNote());
        spnCompanies.setSelection(results.indexOf(insurance.getCompany()));
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;

        if (DateUtils.isDateInFuture(btnDate.getText().toString())) {
            valid = false;
            showMessage("Date should be before the current");
        }

        if (DateUtils.isNotValidDate(btnTime.getText().toString(), false)) {
            valid = false;
            showMessage("Invalid expiration date");
        }else if (!DateUtils.isExpirationDateValid(btnTime.getText().toString())) {
            valid = false;
            showMessage("Expiration day must be at least one day later");
        }

        return super.isInputValid() && valid;
    }

    @Override
    protected void saveItem(Realm realm) {
        Vehicle vehicle = realm.where(Vehicle.class)
                .equalTo(Constants.ID, vehicleId)
                .findFirst();

        Insurance insurance = new Insurance();

        if (isNewItem()) {
            insurance.setId(UUID.randomUUID().toString());
        } else {
            Insurance oldInsurance = vehicle.getInsurances()
                    .where()
                    .equalTo(Constants.ID, itemId)
                    .findFirst();
            RealmUtils.deleteProperty(oldInsurance, Constants.ActivityType.INSURANCE);
            insurance.setId(itemId);
        }

        Date date = DateUtils.stringToDate(btnDate.getText().toString());
        insurance.setDate(date);

        long odometer = Long.parseLong(TextUtils.getTextFromTil(tilOdometer));
        insurance.setOdometer(odometer);
        if (odometer > getVehicleOdometer()) {
            vehicle.setOdometer(odometer);
            setVehicleOdometer(odometer);
        }

        long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
        insurance.setPrice(price);

        insurance.setNote(TextUtils.getTextFromTil(tilNote));

        DateNotification notification = realm.createObject(DateNotification.class, UUID.randomUUID().toString());
        Number number = realm.where(DateNotification.class).max(Constants.NOTIFICATION_ID);
        int notificationId;
        if (number == null) {
            notificationId = 0;
        } else {
            notificationId = number.intValue() + 1;
        }
        notification.setNotificationId(notificationId);
        date = DateUtils.stringToDate(btnTime.getText().toString());
        notification.setDate(date);
        notification.setTriggered(false);
        insurance.setNotification(notification);

        Company company = realm.where(Company.class)
                .equalTo(Constants.ID, companyId)
                .findFirst();
        insurance.setCompany(company);
        vehicle.getInsurances().add(realm.copyToRealmOrUpdate(insurance));
        setNotification(insurance);
    }

    @Override
    protected void onItemSaved() {
        if (isNewItem()) {
            showMessage("New insurance saved!");
        }else {
            showMessage("Insurance updated!");
            Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
            intent.putExtra(Constants.ID, vehicleId);
            intent.putExtra(Constants.ITEM_ID, itemId);
            intent.putExtra(Constants.TYPE, Constants.ActivityType.INSURANCE.ordinal());
            startActivity(intent);
        }

        finish();
    }

    private void setNotification(Insurance insurance) {
        Date notificationDate = insurance.getNotification().getDate();

        Notification notification = NotificationUtils.createNotification(getApplicationContext(),
                vehicleId, Constants.INSURANCES + Constants.ID, insurance.getId(),
                Constants.ActivityType.INSURANCE, ViewActivity.class, "Insurance",
                insurance.getCompany().getName() + " insurance is expiring on " +
                        DateUtils.datetimeToString(insurance.getNotification().getDate()),
                R.drawable.ic_insurance_black);

        NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                insurance.getNotification().getNotificationId(), notificationDate.getTime());
    }

    private ArrayList<String> getCompanyNames() {
        ArrayList<String> names = new ArrayList<>(results.size());
        for (Company company : results) {
            names.add(company.getName());
        }
        return names;
    }
/*
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
        insuranceId = getIntent().getStringExtra(Constants.INSURANCES + Constants.ID);
        if (insuranceId != null) {
            setUpdate(true);
        }
        tilTime = (TextInputLayout) findViewById(R.id.til_new_insurance_time);
        tilExpirationDate = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_date);
        tilExpirationTime = (TextInputLayout) findViewById(R.id.til_new_insurance_expiration_time);
        spnCompanies = (Spinner) findViewById(R.id.spn_new_insurance_company_name);
        imgBtnAddCompany = (ImageButton) findViewById(R.id.img_btn_add_company);
        tilPrice = (TextInputLayout) findViewById(R.id.til_new_insurance_price);
        results = myRealm.where(Company.class).findAllSorted(Constants.NAME, Sort.ASCENDING);
        companiesAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.textview_spinner, getCompanyNames());
        companiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCompanies.setAdapter(companiesAdapter);
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
                        results = myRealm.where(Company.class).findAllSorted(Constants.NAME, Sort.ASCENDING);
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
        tilDate.setHint("Date");
        tilExpirationDate.setHint(getString(R.string.expiration_date));
        tilExpirationTime.setHint(getString(R.string.expiration_time));
        TextView tvCurrentOdometer = (TextView) findViewById(R.id.tv_new_insurance_current_odometer);
        setCurrentOdometer(tvCurrentOdometer);
        if (isUpdate()) {
            Insurance insurance = myRealm.where(Insurance.class)
                    .equalTo(Constants.ID, insuranceId).findFirst();
            TextUtils.setTextToTil(tilDate, DateUtils.dateToString(insurance.getDate()));
            TextUtils.setTextToTil(tilTime, DateUtils.timeToString(insurance.getDate()));
            TextUtils.setTextToTil(tilExpirationDate, DateUtils
                    .dateToString(insurance.getNotification().getDate()));
            TextUtils.setTextToTil(tilExpirationTime, DateUtils
                    .timeToString(insurance.getNotification().getDate()));
            TextUtils.setTextToTil(tilOdometer, String.valueOf(insurance.getOdometer()));
            TextUtils.setTextToTil(tilPrice, MoneyUtils.longToString(new BigDecimal(insurance.getPrice())));
            TextUtils.setTextToTil(tilNote, insurance.getNote());
            spnCompanies.setSelection(results.indexOf(insurance.getCompany()));
        }else {
            Calendar calendar = Calendar.getInstance();
            TextUtils.setTextToTil(tilTime, DateUtils.timeToString(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 5);
            TextUtils.setTextToTil(tilExpirationDate, DateUtils.dateToString(calendar.getTime()));
            TextUtils.setTextToTil(tilExpirationTime, DateUtils.timeToString(calendar.getTime()));
        }
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;
        if (DateUtils.isDateInFuture(TextUtils.getTextFromTil(tilDate),
                TextUtils.getTextFromTil(tilTime))) {
            valid = false;
            tilDate.setError("Date&Time should be before the current");
        }
        String expDate = TextUtils.getTextFromTil(tilExpirationDate) + " " +TextUtils.getTextFromTil(tilExpirationTime);
        if (DateUtils.isNotValidDate(expDate, true)) {
            valid = false;
            tilExpirationDate.setError("Invalid date");
        }
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilPrice))) {
            valid = false;
            tilPrice.setError("Price should be number");
        }
        return super.isInputValid() && valid;
    }

    @Override
    public void saveToRealm() {
        final String companyId = results.get(spnCompanies.getSelectedItemPosition()).getId();
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle vehicle = realm.where(Vehicle.class)
                        .equalTo(Constants.ID, getVehicleId())
                        .findFirst();
                Insurance insurance = new Insurance();
                if (isUpdate()) {
                    Insurance oldInsurance = vehicle.getInsurances()
                            .where()
                            .equalTo(Constants.ID, insuranceId)
                            .findFirst();
                    RealmUtils.deleteProperty(oldInsurance, ActivityType.INSURANCE);
                }else {
                    insuranceId = UUID.randomUUID().toString();
                }
                insurance.setId(insuranceId);

                Date date = DateUtils.stringToDate(TextUtils.getTextFromTil(tilDate));
                Date time = DateUtils.stringToTime(TextUtils.getTextFromTil(tilTime));
                insurance.setDate(DateUtils.dateTime(date, time));
                long odometer = Long.parseLong(TextUtils.getTextFromTil(tilOdometer));
                insurance.setOdometer(odometer);
                if (odometer > getVehicleOdometer()) {
                    vehicle.setOdometer(odometer);
                    setVehicleOdometer(odometer);
                }
                long price = MoneyUtils.stringToLong(TextUtils.getTextFromTil(tilPrice));
                insurance.setPrice(price);
                insurance.setNote(TextUtils.getTextFromTil(tilNote));
                DateNotification notification = realm.createObject(DateNotification.class,
                        UUID.randomUUID().toString());
                int notificationId;
                Number number = realm.where(DateNotification.class)
                        .max(Constants.NOTIFICATION_ID);
                if (number == null) {
                    notificationId = 0;
                }else {
                    notificationId = number.intValue() + 1;
                }
                notification.setNotificationId(notificationId);
                date = DateUtils.stringToDate(TextUtils.getTextFromTil(tilExpirationDate));
                time = DateUtils.stringToTime(TextUtils.getTextFromTil(tilExpirationTime));
                notification.setDate(DateUtils.dateTime(date, time));
                notification.setTriggered(false);
                insurance.setNotification(notification);

                Company company = realm.where(Company.class)
                        .equalTo(Constants.ID, companyId)
                        .findFirst();
                insurance.setCompany(company);
                vehicle.getInsurances().add(realm.copyToRealmOrUpdate(insurance));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Insurance insurance = myRealm.where(Insurance.class)
                        .equalTo(Constants.ID, insuranceId).findFirst();


                Date notificationDate = insurance.getNotification().getDate();
                //Calendar calendar = Calendar.getInstance();
                //calendar.setTime(notificationDate);

                Notification notification = NotificationUtils.createNotification(getApplicationContext(),
                        getVehicleId(), Constants.INSURANCES + Constants.ID, insurance.getId(),
                        ActivityType.INSURANCE, ViewActivity.class, "Insurance",
                        insurance.getCompany().getName() + " insurance is expiring on " +
                                DateUtils.datetimeToString(insurance.getNotification().getDate()),
                        R.drawable.ic_insurance_black);

                NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                        insurance.getNotification().getNotificationId(), notificationDate.getTime());

                if (isUpdate()) {
                    showMessage("Insurance updated!");
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    intent.putExtra(Constants.ID, getVehicleId());
                    intent.putExtra(Constants.INSURANCES + Constants.ID, insuranceId);
                    intent.putExtra(Constants.TYPE, ActivityType.INSURANCE.ordinal());
                    startActivity(intent);
                }else {
                    showMessage("New insurance saved!");
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
    }

    private ArrayList<String> getCompanyNames() {
        ArrayList<String> names = new ArrayList<>(results.size());
        for (Company company : results) {
            names.add(company.getName());
        }
        return names;
    }*/
}
