package stoyanov.valentin.mycar.realm.repositories.impl;

import java.util.UUID;
import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.Vehicle;
import stoyanov.valentin.mycar.realm.repositories.IVehicleRepository;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class VehicleRepository implements IVehicleRepository {

    public static final String addMessage = "New vehicle saved!";
    public static final String updateMessage = "Vehicle is updated!";
    public static final String deleteMessage = "Vehicle is deleted!";

    @Override
    public void addVehicle(final Vehicle vehicle, final OnWritesCallback callback) {
        vehicle.setId(UUID.randomUUID().toString());
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(vehicle);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess(addMessage);
                }
                realmInstance.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (callback != null) {
                    callback.onError(new Exception(error));
                }
                realmInstance.close();
            }
        });
    }

    @Override
    public void deleteVehicleById(final String id, final OnWritesCallback callback) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle vehicle = realm
                        .where(Vehicle.class)
                        .equalTo(RealmTable.ID, id)
                        .findFirst();
                vehicle.deleteFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess(deleteMessage);
                }
                realmInstance.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (callback != null) {
                    callback.onError(new Exception(error));
                }
                realmInstance.close();
            }
        });
    }

    @Override
    public void updateVehicle(final String id, final Vehicle newVehicle,
                              final OnWritesCallback callback) {

        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vehicle oldVehicle = realm
                        .where(Vehicle.class)
                        .equalTo(RealmTable.ID, id)
                        .findFirst();
                oldVehicle.setName(newVehicle.getName());
                oldVehicle.setColor(newVehicle.getColor());
                oldVehicle.setBrand(newVehicle.getBrand());
                oldVehicle.setModel(newVehicle.getModel());
                oldVehicle.setCubicCentimeter(newVehicle.getCubicCentimeter());
                oldVehicle.setExpenses(newVehicle.getExpenses());
                oldVehicle.setFuelTanks(newVehicle.getFuelTanks());
                oldVehicle.setHorsePower(newVehicle.getHorsePower());
                oldVehicle.setInsurances(newVehicle.getInsurances());
                oldVehicle.setManufactureDate(newVehicle.getManufactureDate());
                oldVehicle.setNote(newVehicle.getNote());
                oldVehicle.setOdometer(newVehicle.getOdometer());
                oldVehicle.setPlate(newVehicle.getPlate());
                oldVehicle.setRefuelings(newVehicle.getRefuelings());
                oldVehicle.setReminders(newVehicle.getReminders());
                oldVehicle.setServices(newVehicle.getServices());
                oldVehicle.setType(newVehicle.getType());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess(updateMessage);
                }
                realmInstance.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                if (callback != null) {
                    callback.onError(new Exception(error));
                }
                realmInstance.close();
            }
        });
    }

    @Override
    public void getVehicleById(String id, OnGetSigleVehicleCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        Vehicle vehicle = realmInstance
                .where(Vehicle.class)
                .equalTo(RealmTable.ID, id)
                .findFirst();
        if (callback != null) {
            if (vehicle != null) {
                callback.onSuccess(vehicle);
            }else {
                callback.onError();
            }
        }
    }

    @Override
    public void getAllVehicles(OnGetAllVehiclesCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        RealmResults<Vehicle> results = realmInstance.where(Vehicle.class).findAllAsync();
        if (callback != null) {
            callback.onSuccess(results);
        }
        //close realmInstance
    }
}
