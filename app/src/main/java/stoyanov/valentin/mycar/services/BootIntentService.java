package stoyanov.valentin.mycar.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.DateUtils;
import stoyanov.valentin.mycar.utils.NotificationUtils;

public class BootIntentService extends IntentService {

    private static final String FIELD_NAME = RealmTable.NOTIFICATION + "." + RealmTable.IS_TRIGGERED;

    public BootIntentService() {
        super("BootIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Realm myRealm = Realm.getDefaultInstance();
        RealmResults<Insurance> insurances = myRealm.where(Insurance.class)
                .equalTo(FIELD_NAME, false).findAll();

        for (Insurance insurance : insurances) {
            String vehicleId = myRealm.where(Vehicle.class)
                    .equalTo(RealmTable.INSURANCES + "." + RealmTable.ID, insurance.getId())
                    .findFirst().getId();

            Notification notification = NotificationUtils.createNotification(getApplicationContext(),
                    vehicleId, RealmTable.INSURANCES + RealmTable.ID, insurance.getId(),
                    ViewActivity.ViewType.INSURANCE, ViewActivity.class, "Expiring insurance",
                    insurance.getCompany().getName() + " insurance is expiring on " +
                            DateUtils.datetimeToString(insurance.getNotification().getNotificationDate()),
                    R.drawable.ic_insurance_black);

            Date notificationDate = insurance.getNotification().getNotificationDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notificationDate);

            NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                    insurance.getNotification().getNotificationId(),
                    calendar.getTimeInMillis());
        }
    }
}
