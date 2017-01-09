package stoyanov.valentin.mycar.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.receivers.NotificationReceiver;

public abstract class BaseActivity extends AppCompatActivity {
    abstract protected void initComponents();
    abstract protected void setComponentListeners();

    /*protected boolean isInputValid() {
        return true;
    }*/

    protected Notification newNotification(String title, String content, int smallIcon) {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        return builder.build();
    }

    protected void addNotification(Notification notification, long triggerAtMillis) {
        Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
    }

    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
