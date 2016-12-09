package stoyanov.valentin.mycar.realm.repositories;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.Vehicle;

public interface IVehicleRepository {

    interface OnWritesCallback {
        void onSuccess(String message);
        void onError(Exception e);
    }

    interface OnGetSigleVehicleCallback {
        void onSuccess(Vehicle vehicle);
        void onError();
    }

    interface OnGetAllVehiclesCallback {
        void onSuccess(RealmResults<Vehicle> vehicles);
    }

    void addVehicle(Vehicle vehicle, OnWritesCallback callback);
    void deleteVehicleById(String id, OnWritesCallback callback);
    void updateVehicle(String id, Vehicle vehicle, OnWritesCallback callback);
    void getVehicleById(String id, OnGetSigleVehicleCallback callback);
    void getAllVehicles(OnGetAllVehiclesCallback callback);
}
