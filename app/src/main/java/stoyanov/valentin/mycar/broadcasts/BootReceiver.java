package stoyanov.valentin.mycar.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import stoyanov.valentin.mycar.services.ResetAlarmManagerService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, ResetAlarmManagerService.class));
        }
    }
}
