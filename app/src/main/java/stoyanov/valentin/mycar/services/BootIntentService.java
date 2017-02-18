package stoyanov.valentin.mycar.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import io.realm.Realm;
import stoyanov.valentin.mycar.broadcasts.BootReceiver;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.RealmUtils;

public class BootIntentService extends IntentService {

    private static final String TAG = "BootIntentService";
    //private static final String FIELD_NAME = RealmTable.NOTIFICATION + "." + RealmTable.IS_TRIGGERED;

    public BootIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        Realm myr = Realm.getDefaultInstance();
        //SUUUII works !!!
        myr.close();
        BootReceiver.completeWakefulIntent(intent);
    }
}
