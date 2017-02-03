package stoyanov.valentin.mycar.activities.abstracts;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
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

public abstract class NewBaseActivity extends BaseActivity
                    implements INewBaseActivity{

    private boolean update = false;
    private String vehicleId;
    private long vehicleOdometer;
    protected OnOdometerChangeListener listener;
    protected Realm myRealm;
    protected TextInputLayout tilDate, tilOdometer, tilNote;

    @Override
    public void initComponents() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tilDate = (TextInputLayout) findViewById(R.id.til_date);
        Calendar calendar = Calendar.getInstance();
        TextUtils.setTextToTil(tilDate, DateUtils.dateToString(calendar.getTime()));
        tilOdometer = (TextInputLayout) findViewById(R.id.til_odometer);
        tilNote = (TextInputLayout) findViewById(R.id.til_note);
        Intent intent = getIntent();
        vehicleId = intent.getStringExtra(RealmTable.ID);
        vehicleOdometer = intent.getLongExtra(RealmTable.ODOMETER, 0);
        myRealm = Realm.getDefaultInstance();
        listener = new OnOdometerChangeListener() {
            @Override
            public void onChange(long odometer) {
                RealmSettings settings = myRealm.where(RealmSettings.class).findFirst();
                RealmResults<Service> services = myRealm.where(Service.class)
                        .lessThanOrEqualTo(RealmTable.ODOMETER_NOTIFICATION + "." +
                                RealmTable.TARGET_ODOMETER,
                                odometer + settings.getDistanceInAdvance())
                        .equalTo(RealmTable.ODOMETER_NOTIFICATION + "." + RealmTable.IS_TRIGGERED,
                                false)
                        .findAll();
                for (final Service service : services) {
                    Notification notification = NotificationUtils
                            .createNotification(
                                    getApplicationContext(),
                                    vehicleId, RealmTable.SERVICES + RealmTable.ID,
                                    service.getId(), ViewActivity.ViewType.SERVICE,
                                    ViewActivity.class, "Service", service.getType()
                                            .getName() + " should be revised at " + service
                                            .getOdometerNotification().getTargetOdometer(),
                                    R.drawable.ic_services_black
                            );
                    NotificationManager manager = (NotificationManager) getApplicationContext()
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(0, notification);
                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            service.getOdometerNotification().setTriggered(true);
                        }
                    });
                }
            }
        };
    }

    @Override
    public void setComponentListeners() {
        DateTimePickerUtils.addDatePickerListener(NewBaseActivity.this, tilDate, new Date(),
                DateTimePickerUtils.PickerLimits.MAX);
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    protected String getVehicleId() {
        return vehicleId;
    }

    protected void setCurrentOdometer(TextView textView) {
        String text = String.format(getString(R.string.current_odometer_placeholder),
                vehicleOdometer);
        textView.setText(String.valueOf(text));
    }

    public long getVehicleOdometer() {
        return vehicleOdometer;
    }

    public void setVehicleOdometer(long vehicleOdometer) {
        this.vehicleOdometer = vehicleOdometer;
    }

    public boolean isInputValid() {
        boolean valid = true;
        if (!NumberUtils.isCreatable(TextUtils.getTextFromTil(tilOdometer))) {
            valid = false;
            tilOdometer.setError("Numeric value expected");
        }
        if (NumberUtils.createLong(TextUtils.getTextFromTil(tilOdometer)) < NumberUtils.LONG_ZERO) {
            valid = false;
            tilOdometer.setError("Odometer can't be negative");
        }
        /*if (ValidationUtils.isInputValid(getTextFromTil(tilNote))
                || !TextUtils.isEmpty(getTextFromTil(tilNote))) {
            valid = false;
            tilNote.setError("Invalid characters");
        }*/
        return valid;
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

    protected interface OnOdometerChangeListener{
        void onChange(long odometer);
    }
}
