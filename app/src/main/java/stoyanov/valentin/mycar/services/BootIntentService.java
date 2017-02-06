package stoyanov.valentin.mycar.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;

import java.util.Calendar;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.broadcasts.BootReceiver;
import stoyanov.valentin.mycar.realm.table.RealmTable;
import stoyanov.valentin.mycar.utils.NotificationUtils;

public class BootIntentService extends IntentService {

    private static final String FIELD_NAME = RealmTable.NOTIFICATION + "." + RealmTable.IS_TRIGGERED;

    public BootIntentService() {
        super("BootIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Notification notification = NotificationUtils.createNotification(getApplicationContext(),
                "asdsadad", RealmTable.INSURANCES + RealmTable.ID, "sadasdasd",
                ViewActivity.ViewType.INSURANCE, ViewActivity.class, "Expiring insurance",
                 " insurance is expiring on ",
                R.drawable.ic_insurance_black);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                19239,
                calendar.getTimeInMillis());
        //Log.d("Service: ", "we are in the service");

       /*Realm myRealm = Realm.getDefaultInstance();
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
                            DateUtils.datetimeToString(insurance.getNotification().getDate()),
                    R.drawable.ic_insurance_black);

            Date notificationDate = insurance.getNotification().getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notificationDate);

            NotificationUtils.setNotificationOnDate(getApplicationContext(), notification,
                    insurance.getNotification().getNotificationId(),
                    calendar.getTimeInMillis());
        }
        myRealm.close();*/
        BootReceiver.completeWakefulIntent(intent);
    }
}
