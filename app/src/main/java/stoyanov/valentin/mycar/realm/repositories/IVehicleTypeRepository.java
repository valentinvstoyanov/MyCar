package stoyanov.valentin.mycar.realm.repositories;

import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.VehicleType;

public interface IVehicleTypeRepository {

    interface OnVehicleTypesAdded {
        void onSuccess();
        void onError();
    }

    interface OnGetAllVehicleTypes {
        void onSuccess(RealmResults<VehicleType> results);
    }

    void addManyVehicleTypes(String[] names, OnVehicleTypesAdded callback);
    void getAllVehicleTypes(OnGetAllVehicleTypes callback);
}
