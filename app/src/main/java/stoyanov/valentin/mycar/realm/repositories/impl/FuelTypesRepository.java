package stoyanov.valentin.mycar.realm.repositories.impl;

import java.util.UUID;
import io.realm.Realm;
import stoyanov.valentin.mycar.realm.models.FuelType;
import stoyanov.valentin.mycar.realm.repositories.IFuelTypeRepository;

public class FuelTypesRepository implements IFuelTypeRepository{

    @Override
    public void addManyFuelTypes(final String[] names, final OnAddManyFuelTypesCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String name : names) {
                    FuelType fuelType = realm.createObject(FuelType.class, UUID.randomUUID().toString());
                    fuelType.setName(name);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                callback.onError();
            }
        });
    }
}
