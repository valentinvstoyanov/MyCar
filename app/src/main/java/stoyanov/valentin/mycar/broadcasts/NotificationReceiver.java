package stoyanov.valentin.mycar.broadcasts;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import stoyanov.valentin.mycar.utils.NotificationUtils;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_ID = "notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        NotificationUtils.triggerNotification(context, id, notification);
    }
}
