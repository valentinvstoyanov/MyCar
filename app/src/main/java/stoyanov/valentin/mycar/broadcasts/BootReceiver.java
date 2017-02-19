package stoyanov.valentin.mycar.broadcasts;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import stoyanov.valentin.mycar.services.ResetAlarmManagerService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "samolevski");
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i(TAG, "Boot event received!");
            Log.e(TAG, "samolevski");
            Intent serviceIntent = new Intent(context, ResetAlarmManagerService.class);
            ComponentName componentName = context.startService(serviceIntent);
            //.startService(serviceIntent);
            if (componentName != null) {
                Log.i(TAG, "ResetAlarmManagerService successfully started!");
            }else {
                Log.i(TAG, "ResetAlarmManagerService is not running...");
            }
            //startWakefulService(context, serviceIntent);
        }
    }
}
