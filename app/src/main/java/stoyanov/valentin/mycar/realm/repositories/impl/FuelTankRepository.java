package stoyanov.valentin.mycar.realm.repositories.impl;

import java.util.UUID;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.FuelTank;
import stoyanov.valentin.mycar.realm.models.FuelType;
import stoyanov.valentin.mycar.realm.repositories.IFuelTankRepository;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class FuelTankRepository implements IFuelTankRepository {

    private Realm realmInstance;

    public FuelTankRepository(Realm realmInstance) {
        this.realmInstance = realmInstance;
    }

    @Override
    public void addFuelTank(FuelTank fuelTank) {
        Realm realmInstance = Realm.getDefaultInstance();
        fuelTank.setId(UUID.randomUUID().toString());
        realmInstance.beginTransaction();
        realmInstance.copyToRealm(fuelTank);
        realmInstance.commitTransaction();
        realmInstance.close();
    }

/*    @Override
    public void addManyFuelTanks(FuelTank[] fuelTanks, OnAddManyFuelTanksCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.beginTransaction();
        for (FuelTank fuelTank : fuelTanks) {
            fuelTank.setId(UUID.randomUUID().toString());
            realmInstance.copyToRealm(fuelTank);
        }
        realmInstance.commitTransaction();
        callback.onSuccess(fuelTanks);
        realmInstance.close();
    }*/

    @Override
    public void addManyFuelTanks(final FuelTank[] fuelTanks, final OnAddManyFuelTanksCallback callback) {
       // Realm realmInstance = Realm.getDefaultInstance();
        final RealmList<FuelTank> l = new RealmList<>();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (FuelTank fuelTank : fuelTanks) {
                    FuelTank realmFuelTank = realm
                            .createObject(FuelTank.class, UUID.randomUUID().toString());
                    realmFuelTank.setConsumption(fuelTank.getConsumption());
                    realmFuelTank.setCapacity(fuelTank.getCapacity());
                    FuelType fuelType = realm
                            .where(FuelType.class)
                            .equalTo(RealmTable.NAME, fuelTank.getFuelType().getName())
                            .findFirst();
                    realmFuelTank.setFuelType(fuelType);
                    l.add(realmFuelTank);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //???????
                callback.onSuccess(l);
            }
        });
    }
}
