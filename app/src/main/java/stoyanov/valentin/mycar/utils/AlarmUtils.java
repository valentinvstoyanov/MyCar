package stoyanov.valentin.mycar.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;

public class AlarmUtils {

    public static void setDailyCheckAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, null);
    }
}
