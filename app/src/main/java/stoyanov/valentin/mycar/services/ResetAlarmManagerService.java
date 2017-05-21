package stoyanov.valentin.mycar.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;

public class ResetAlarmManagerService extends IntentService {

    private static final String TAG = "ResetAlarmService";
    private static final String IS_TRIGGERED = Constants.DATE_NOTIFICATION + Constants.IS_TRIGGERED;

    public ResetAlarmManagerService() {
        super("ResetAlarmManagerService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "stromba=stromba");
        Realm myRealm = Realm.getDefaultInstance();

        RealmResults<Service> services = myRealm
                .where(Service.class)
                .equalTo(Constants.SHOULD_NOTIFY, true)
                .isNotNull(Constants.DATE_NOTIFICATION)
                .equalTo(IS_TRIGGERED, false)
                .findAll();
        for (Service service : services) {
            String vehicleId = myRealm
                    .where(Vehicle.class)
                    .equalTo(Constants.SERVICES + "." + Constants.ID, service.getId())
                    .findFirst()
                    .getId();
            Notification notification = NotificationUtils.createNotification
                    (
                            getApplicationContext(), vehicleId,
                            Constants.SERVICES + Constants.ID, service.getId(),
                            Constants.ActivityType.SERVICE, ViewActivity.class, "Service",
                            service.getType().getName() + " should be revised at "
                                    + DateUtils.datetimeToString
                                    (
                                            service.getDateNotification().getDate()
                                    ), R.drawable.ic_services_black
                    );
            NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                    service.getDateNotification().getNotificationId(),
                    service.getDateNotification().getDate().getTime());
        }
        Log.i(TAG, "Service alarms set, services.size() = " + services.size());

        RealmResults<Insurance> insurances = myRealm
                .where(Insurance.class)
                .equalTo(IS_TRIGGERED, false)
                .findAll();
        for (Insurance insurance : insurances) {
            String vehicleId = myRealm
                    .where(Vehicle.class)
                    .equalTo(Constants.SERVICES + "." + Constants.ID, insurance.getId())
                    .findFirst()
                    .getId();

            Notification notification = NotificationUtils.createNotification
                    (
                            getApplicationContext(), vehicleId,
                            Constants.INSURANCES + Constants.ID, insurance.getId(),
                            Constants.ActivityType.INSURANCE, ViewActivity.class, "Insurance",
                            insurance.getCompany().getName() + " insurance is expiring on " +
                                    DateUtils.datetimeToString(insurance.getNotification()
                                            .getDate()),
                            R.drawable.ic_insurance_black
                    );
            NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                    insurance.getNotification().getNotificationId(),
                    insurance.getNotification().getDate().getTime());
        }
        Log.i(TAG, "Insurance alarms set, insurances.size() = " + insurances.size());
        Log.i(TAG, "Ready!");
        myRealm.close();
        stopSelf();
    }
}
