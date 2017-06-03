package stoyanov.valentin.mycar.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;

import io.realm.Realm;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.ViewActivity;
import stoyanov.valentin.mycar.broadcasts.NotificationReceiver;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.DateNotification;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Service;

public class NotificationUtils {

    private static final String SERVICE = "Service";
    private static final String INSURANCE = "Insurance";
    private static final String SERVICE_DATE_NOTIFICATION_MSG = "Reminder: %s.\n%s should be checked!";
    private static final String SERVICE_ODOMETER_NOTIFICATION_MSG = "%s, the following service should be checked at %d%s!";
    private static final String INSURANCE_NOTIFICATION_MSG = "Reminder: %s.\n%s is expiring!";

    public static void triggerDateServiceNotification(int notificationId, String vehicleId, Service service, Context context) {
        String content = String.format(SERVICE_DATE_NOTIFICATION_MSG, DateUtils.dateToString(service.getDateNotification().getDate()), service.getType().getName());
        Notification notification = createNotification(context, vehicleId, service.getId(), Constants.ActivityType.SERVICE, SERVICE, content, R.drawable.ic_services_black);
        triggerNotification(notificationId, notification, context);
    }

    public static void triggerInsuranceNotification(int notificationId, String vehicleId, Insurance insurance, Context context) {
        String content = String.format(INSURANCE_NOTIFICATION_MSG, DateUtils.dateToString(insurance.getNotification().getDate()), insurance.getCompany());
        Notification notification = createNotification(context, vehicleId, insurance.getId(), Constants.ActivityType.SERVICE, SERVICE, content, R.drawable.ic_insurance_black);
        triggerNotification(notificationId, notification, context);
    }

    public static void triggerNotification(int notificationId, Notification notification, Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, notification);
    }

    public static Notification createNotification(Context context, String vehicleId, String itemId,
                                                  Constants.ActivityType activityType,
                                                  String title, String content, int smallIcon) {

        Intent resultIntent = new Intent(context, ViewActivity.class);
        resultIntent.putExtra(Constants.ID, vehicleId);
        resultIntent.putExtra(Constants.ITEM_ID, itemId);
        resultIntent.putExtra(Constants.TYPE, activityType.ordinal());

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    public static Notification createNotification(Context context, String vehicleId,
                                                  String propertyKey, String propertyId,
                                                  Constants.ActivityType activityType, Class aClass,
                                                  String title, String content,
                                                  int smallIcon) {

        Intent resultIntent = new Intent(context, aClass);
        resultIntent.putExtra(Constants.ID, vehicleId);
        resultIntent.putExtra(propertyKey, propertyId);
        resultIntent.putExtra(Constants.TYPE, activityType.ordinal());

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        return builder.build();
    }

    public static void triggerNotification(Context context, final int notificationId,
                                           Notification notification) {
        final Realm myRealm = Realm.getDefaultInstance();
        final DateNotification dateNotification = myRealm.where(DateNotification.class)
                .equalTo(Constants.NOTIFICATION_ID, notificationId)
                .findFirst();
        if (!dateNotification.isTriggered()) {
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(notificationId, notification);
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    dateNotification.setTriggered(true);
                }
            });
        }
        myRealm.close();
    }

    public static void updateNotification(Context context, Notification notification,
                                          int notificationId) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, notification);
    }

    public static void cancelNotification(Context context, int notificationId,
                                          Notification notification) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

    public static void setNotificationOnDate(Context context, Notification notification,
                                             int notificationId, long triggerAtMillis) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }
}
