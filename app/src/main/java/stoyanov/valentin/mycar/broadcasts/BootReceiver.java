package stoyanov.valentin.mycar.broadcasts;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import stoyanov.valentin.mycar.R;

public class BootReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "BootReceiver: ";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: BOOOOOOOOOOOOOOOOOOOOOOOOOOT OAOSDOASODSAODAOSDOASDOASODOSADOADO");
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle("BOOT");
            builder.setContentText("Boot receiver");
            builder.setSmallIcon(R.drawable.ic_upload_black_24dp);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher));
            Notification n = builder.build();
            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1422, n);
            //boot completed, start resetAlarmService
            //Intent resetAlarmsServiceIntent = new Intent(context, BootIntentService.class);
            //startWakefulService(context, resetAlarmsServiceIntent); //startWakefulService() guarantees that service will start
        }
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "PISNA MIIII", Toast.LENGTH_LONG).show();
           *//* Intent serviceIntent = new Intent(context, BootIntentService.class);
            ComponentName componentName = context.startService(serviceIntent);
            if (componentName == null) {
                Log.e(TAG, "Couldn't start service");
            }else {
                Log.i(TAG, "Service has started successfully");
            }*//*
        }else {
            Log.i(TAG, "Unexpected intent action received: " + intent.getAction());
        }
    }*/
}
