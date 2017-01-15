package stoyanov.valentin.mycar.activities.abstracts;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;

import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.activities.MainActivity;
import stoyanov.valentin.mycar.activities.interfaces.IBaseActivity;
import stoyanov.valentin.mycar.receivers.NotificationReceiver;

public abstract class BaseActivity extends AppCompatActivity
                    implements IBaseActivity{

/*    abstract protected void initComponents();
    abstract protected void setComponentListeners();
    abstract protected void setContent();*/

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
        /*Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        NotificationReceiver receiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter("ALARM_ACTION");
        registerReceiver(receiver, filter);

        Intent intent = new Intent("ALARM_ACTION");
        intent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        intent.putExtra(NotificationReceiver.NOTIFICATION_ID, System.currentTimeMillis());
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + triggerAtMillis,
                operation);
        Log.d("asd", "addNotification: " + System.currentTimeMillis() + ", " + triggerAtMillis);
        // I choose 3s after the launch of my application
       /* alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000, operation) ;
        Calendar calendar = Calendar.getInstance();

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis - calendar.getTimeInMillis(), pendingIntent);
        */
    }

    protected void setToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
