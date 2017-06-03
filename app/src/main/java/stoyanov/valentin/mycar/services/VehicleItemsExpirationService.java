package stoyanov.valentin.mycar.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.Constants;
import stoyanov.valentin.mycar.realm.models.Insurance;
import stoyanov.valentin.mycar.realm.models.Service;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.utils.NotificationUtils;

public class VehicleItemsExpirationService extends IntentService{

    private static final String TAG = "VehicleItemsExpirationService";

    public VehicleItemsExpirationService() {
        super(TAG);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, TAG + " starting...", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Realm myRealm = Realm.getDefaultInstance();
        RealmResults<Vehicle> vehicles = myRealm.where(Vehicle.class).findAll();
        for (Vehicle vehicle : vehicles) {
            processVehicle(vehicle, myRealm);
        }
        myRealm.close();
    }

    private void processVehicle(Vehicle vehicle, Realm myRealm) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        RealmResults<Service> services = vehicle.getServices()
                .where()
                .equalTo(Constants.SHOULD_NOTIFY, true)
                .isNotNull(Constants.DATE_NOTIFICATION)
                .equalTo(Constants.DATE_NOTIFICATION + Constants.IS_TRIGGERED, false)
                .lessThanOrEqualTo(Constants.DATE_NOTIFICATION + Constants.DATE, today.getTime())
                .findAll();
        int i = 0;
        for (Service service : services) {
            notifyServiceExpired(i, vehicle.getId(), service);
            i++;
        }

        RealmResults<Insurance> insurances = vehicle.getInsurances()
                .where()
                .equalTo(Constants.SHOULD_NOTIFY, true)
                .equalTo(Constants.DATE_NOTIFICATION + Constants.IS_TRIGGERED, false)
                .lessThanOrEqualTo(Constants.DATE_NOTIFICATION + Constants.DATE, today.getTime())
                .findAll();
        for (Insurance insurance : insurances) {
            notifyInsuranceExpired(i, vehicle.getId(), insurance);
            i++;
        }
    }

    private void notifyServiceExpired(int notificationId, String vehicleId, Service service) {
        NotificationUtils.triggerDateServiceNotification(notificationId, vehicleId, service, getApplicationContext());
    }

    private void notifyInsuranceExpired(int notificationId, String vehicleId, Insurance insurance) {
        NotificationUtils.triggerInsuranceNotification(notificationId, vehicleId, insurance, getApplicationContext());
    }
}
