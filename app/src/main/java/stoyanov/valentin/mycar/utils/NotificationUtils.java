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
import stoyanov.valentin.mycar.realm.models.RealmNotification;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class NotificationUtils {

    public static Notification createNotification(Context context, String vehicleId,
                                                  String propertyKey, String propertyId,
                                                  ViewActivity.ViewType viewType, Class aClass,
                                                  String title, String content,
                                                  int smallIcon) {

        Intent resultIntent = new Intent(context, aClass);
        resultIntent.putExtra(RealmTable.ID, vehicleId);
        resultIntent.putExtra(propertyKey, propertyId);
        resultIntent.putExtra(RealmTable.TYPE, viewType.ordinal());

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
        final RealmNotification realmNotification = myRealm.where(RealmNotification.class)
                .equalTo(RealmTable.NOTIFICATION_ID, notificationId)
                .findFirst();
        if (!realmNotification.isTriggered()) {
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(notificationId, notification);
            myRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmNotification.setTriggered(true);
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