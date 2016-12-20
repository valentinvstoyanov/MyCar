package stoyanov.valentin.mycar.realm.repositories.impl;

import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.realm.models.Model;
import stoyanov.valentin.mycar.realm.repositories.IModelRepository;
import stoyanov.valentin.mycar.realm.table.RealmTable;

public class ModelRepository implements IModelRepository {

    @Override
    public void addOrGetModel(String name, OnAddOrGetModelCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        Model model = realmInstance.where(Model.class).equalTo(RealmTable.NAME, name).findFirst();
        if (model == null) {
            realmInstance.beginTransaction();
            model = realmInstance.createObject(Model.class, UUID.randomUUID().toString());
            model.setName(name);
            realmInstance.commitTransaction();
        }
        callback.onSuccess(model);
        realmInstance.close();
    }

    @Override
    public void getAllModels(OnGetAllModelsCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        callback.onSuccess(realmInstance.where(Model.class).findAll());
        realmInstance.close();
    }

    public static String[] getModelNames(Model[] models) {
        String[] names = new String[models.length];
        int i = 0;
        for (Model model : models) {
            names[i] = model.getName();
            i++;
        }
        return names;
    }
}
