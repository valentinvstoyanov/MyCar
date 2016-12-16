package stoyanov.valentin.mycar.realm.repositories.impl;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.repositories.IBrandRepository;

public class BrandRepository implements IBrandRepository{

    @Override
    public void addBrand(String name) {
        Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.beginTransaction();
        Brand brand = realmInstance.createObject(Brand.class);
        brand.setId(UUID.randomUUID().toString());
        brand.setName(name);
        realmInstance.commitTransaction();
        realmInstance.close();
    }

    @Override
    public void addManyBrands(final String[] names, final OnAddBrandListCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String name : names) {
                    Brand brand = realm.createObject(Brand.class);
                    brand.setId(UUID.randomUUID().toString());
                    brand.setName(name);
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

    @Override
    public void getAllBrands(OnGetAllBrandsCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        callback.onSuccess(realmInstance.where(Brand.class).findAllAsync());
    }

    public static String[] getBrandNames(Brand[] brands) {
        String[] names = new String[brands.length];
        int i = 0;
        for (Brand brand : brands) {
            names[i] = brand.getName();
            i++;
        }
        return names;
    }
}
