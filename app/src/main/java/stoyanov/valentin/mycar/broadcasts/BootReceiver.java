package stoyanov.valentin.mycar.broadcasts;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import stoyanov.valentin.mycar.services.BootIntentService;

public class BootReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "EVENT RECEIVED BLA BLA . . . . .");
            Intent serviceIntent = new Intent(context, BootIntentService.class);
            startWakefulService(context, serviceIntent);
        }
    }
}
