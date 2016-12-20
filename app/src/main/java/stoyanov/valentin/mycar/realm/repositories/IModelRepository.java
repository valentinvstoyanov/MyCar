package stoyanov.valentin.mycar.realm.repositories;

import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.Model;

public interface IModelRepository {

    interface OnAddOrGetModelCallback {
        void onSuccess(Model model);
    }

    interface OnGetAllModelsCallback {
        void onSuccess(RealmResults<Model> results);
    }

    void addOrGetModel(String name, OnAddOrGetModelCallback callback);
    void getAllModels(OnGetAllModelsCallback callback);
}
