package stoyanov.valentin.mycar.realm.repositories;

public interface IFuelTypeRepository {

    void addManyFuelTypes(String[] names, OnAddManyFuelTypesCallback callback);

    interface OnAddManyFuelTypesCallback {
        void onSuccess();
        void onError();
    }
}
