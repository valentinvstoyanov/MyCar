package stoyanov.valentin.mycar.broadcasts;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import stoyanov.valentin.mycar.services.BootIntentService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver: ";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, BootIntentService.class);
            ComponentName componentName = context.startService(serviceIntent);
            if (componentName == null) {
                Log.i(TAG, "Couldn't start service");
            }else {
                Log.i(TAG, "Service has started successfully");
            }
        }else {
            Log.i(TAG, "Unexpected intent action received: " + intent.getAction());
        }
    }
}
