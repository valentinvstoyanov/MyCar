package stoyanov.valentin.mycar.activities.abstracts;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import org.apache.commons.lang3.math.NumberUtils;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.ActivityType;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.activities.interfaces.INewBaseActivity;
import stoyanov.valentin.mycar.realm.models.RealmSettings;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateTimePickerUtils;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;
import stoyanov.valentin.mycar.utils.TextUtils;

public abstract class AddEditBaseActivity extends BaseActivity implements INewBaseActivity {

    private long vehicleOdometer;
    protected String vehicleId;
    protected String itemId;
    protected Realm myRealm;
    protected ProgressBar progressBar;
    protected Button btnDate;
    protected Button btnTime;
    protected TextView tvCurrentOdometer;
    protected TextInputLayout tilOdometer;
    protected TextInputLayout tilPrice;
    protected TextInputLayout tilNote;

    protected abstract void populateNewItem();
    protected abstract void populateExistingItem();
    protected abstract void saveItem(Realm realm);
    protected abstract void onItemSaved();

    @Override
    public void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setBackNavigation();
        btnDate = (Button) findViewById(R.id.btn_add_edit_date);
        btnTime = (Button) findViewById(R.id.btn_add_edit_time);
        tvCurrentOdometer = (TextView) findViewById(R.id.tv_add_edit_current_odometer);
        tilOdometer = (TextInputLayout) findViewById(R.id.til_add_edit_odometer);
        tilPrice = (TextInputLayout) findViewById(R.id.til_add_edit_price);
        tilNote = (TextInputLayout) findViewById(R.id.til_add_edit_note);
        progressBar = (ProgressBar) findViewById(R.id.pb_add_edit);
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
        itemId = intent.getStringExtra(RealmTable.EXPENSES + RealmTable.ID);
        vehicleOdometer = intent.getLongExtra(RealmTable.ODOMETER, 0);
        myRealm = Realm.getDefaultInstance();
    }

    @Override
    public void setComponentListeners() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerUtils.showDatePicker(AddEditBaseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = DateUtils.getDateFromInts(year, month, dayOfMonth);
                        btnDate.setText(date);
                    }
                });
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerUtils.showTimePicker(AddEditBaseActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = DateUtils.getTimeFromInts(hourOfDay, minute);
                        btnTime.setText(time);
                    }
                });
            }
        });
    }

    @Override
    public void setContent() {
        String text = String.format(getString(R.string.current_odometer_placeholder), vehicleOdometer);
        tvCurrentOdometer.setText(String.valueOf(text));
        if (isNewItem()) {
            populateNewItem();
        }else {
            populateExistingItem();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRealm.close();
    }

    @Override
    public boolean isInputValid() {
        boolean valid = true;
        if (DateUtils.isNotValidDate(btnDate.getText().toString(), false)) {
            valid = false;
            showMessage("Invalid date");
        }

        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilOdometer))) {
            valid = false;
            tilOdometer.setError("Numeric value expected");
        }else {
            if (NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer)) < NumberUtils.LONG_ZERO) {
                valid = false;
                tilOdometer.setError("Odometer cannot be negative");
            }
        }

        if (TextUtils.getTextFromTil(tilNote).length() > 256) {
            valid = false;
            tilNote.setError("Too many characters");
        }

        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilPrice))) {
            valid = false;
            tilPrice.setError("Price should be number");
        }

        return valid;
    }

    @Override
    public void saveToRealm() {
        myRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                saveItem(realm);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                new ServiceNotifyingAsyncTask().execute();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                progressBar.setIndeterminate(false);
                showMessage("Something went wrong...");
                error.printStackTrace();
            }
        });
    }

    public void setVehicleOdometer(long vehicleOdometer) {
        this.vehicleOdometer = vehicleOdometer;
       // onOdometerChange();
    }

    public long getVehicleOdometer() {
        return vehicleOdometer;
    }

   /* public void onOdometerChange() {
        new ServiceNotifyingAsyncTask().execute();
    }*/

    public boolean isNewItem() {
        return itemId == null;
    }

    private class ServiceNotifyingAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Realm realmDb = Realm.getDefaultInstance();
            RealmSettings settings = realmDb.where(RealmSettings.class).findFirst();
            long targetOdometer = vehicleOdometer + settings.getDistanceInAdvance();
            RealmResults<Service> services = realmDb
                    .where(Service.class)
                    .equalTo(RealmTable.SHOULD_NOTIFY, true)
                    .equalTo(RealmTable.IS_ODOMETER_TRIGGERED, false)
                    .notEqualTo(RealmTable.TARGET_ODOMETER, 0)
                    .lessThanOrEqualTo(RealmTable.TARGET_ODOMETER, targetOdometer)
                    .findAll();
            String text = "%s should be revised at %d " + settings.getLengthUnit();
            int i = 0;
            for (final Service service : services) {
                Notification notification = NotificationUtils.createNotification
                        (
                                getApplicationContext(), vehicleId,
                                RealmTable.SERVICES + RealmTable.ID, service.getId(),
                                ActivityType.SERVICE, ViewActivity.class, "Service",
                                String.format(text, service.getType().getName(),
                                        service.getTargetOdometer()),
                                R.drawable.ic_services_black
                        );
                NotificationManager manager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(i, notification);
                realmDb.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        service.setOdometerTriggered(true);
                    }
                });
                i++;
            }
            realmDb.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setIndeterminate(false);
            onItemSaved();
        }
    }
}

/*
        RealmSettings settings = myRealm.where(RealmSettings.class).findFirst();
        long targetOdometer = vehicleOdometer + settings.getDistanceInAdvance();
        RealmResults<Service> services = myRealm
                .where(Service.class)
                .equalTo(RealmTable.SHOULD_NOTIFY, true)
                .equalTo(RealmTable.IS_ODOMETER_TRIGGERED, false)
                .notEqualTo(RealmTable.TARGET_ODOMETER, 0)
                .lessThanOrEqualTo(RealmTable.TARGET_ODOMETER, targetOdometer)
                .findAll();
        String text = "%s should be revised at %d " + settings.getLengthUnit();
        int i = 0;
        for (final Service service : services) {
            Notification notification = NotificationUtils.createNotification
                    (
                            getApplicationContext(), vehicleId,
                            RealmTable.SERVICES + RealmTable.ID, service.getId(),
                            ActivityType.SERVICE, ViewActivity.class, "Service",
                            String.format(text, service.getType().getName(),
                                    service.getTargetOdometer()),
                            R.drawable.ic_services_black
                    );
            NotificationManager manager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(i, notification);
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    service.setOdometerTriggered(true);
                }
            });
            i++;
        }*/
