package stoyanov.valentin.mycar.realm.repositories;

import io.realm.RealmList;
import stoyanov.valentin.mycar.realm.models.FuelTank;

public interface IFuelTankRepository {

    interface OnAddManyFuelTanksCallback{
        void onSuccess(RealmList fuelTanks);
    }

    void addFuelTank(FuelTank fuelTank);
    void addManyFuelTanks(FuelTank[] fuelTanks, OnAddManyFuelTanksCallback callback);
}
