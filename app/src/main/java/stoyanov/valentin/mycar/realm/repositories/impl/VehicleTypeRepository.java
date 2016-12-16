package stoyanov.valentin.mycar.realm.repositories.impl;

import java.util.UUID;
import io.realm.Realm;
import stoyanov.valentin.mycar.realm.models.VehicleType;
import stoyanov.valentin.mycar.realm.repositories.IVehicleTypeRepository;

public class VehicleTypeRepository implements IVehicleTypeRepository {
    @Override
    public void addManyVehicleTypes(final String[] names, final OnVehicleTypesAdded callback) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String name : names) {
                    VehicleType vehicleType = realm.createObject(VehicleType.class);
                    vehicleType.setId(UUID.randomUUID().toString());
                    vehicleType.setName(name);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                realmInstance.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                callback.onError();
                realmInstance.close();
            }
        });
    }

    @Override
    public void getAllVehicleTypes(OnGetAllVehicleTypes callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        callback.onSuccess(realmInstance.where(VehicleType.class).findAllAsync());
    }

}
