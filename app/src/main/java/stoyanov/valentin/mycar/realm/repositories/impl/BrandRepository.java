package stoyanov.valentin.mycar.realm.repositories.impl;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.repositories.IBrandRepository;
import stoyanov.valentin.mycar.realm.table.RealmTable;

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
    public void addOrGetBrand(final String name, OnAddOrGetBrandCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        Brand brand = realmInstance.where(Brand.class).equalTo(RealmTable.NAME, name).findFirst();
        if (brand == null) {
            realmInstance.beginTransaction();
            brand = realmInstance.createObject(Brand.class, UUID.randomUUID().toString());
            brand.setName(name);
            realmInstance.commitTransaction();
        }
        callback.onSuccess(brand);
        realmInstance.close();
    }

    @Override
    public void addManyBrands(final String[] names, final OnAddBrandListCallback callback) {
        final Realm realmInstance = Realm.getDefaultInstance();
        realmInstance.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String name : names) {
                    Brand brand = realm.createObject(Brand.class, UUID.randomUUID().toString());
                    brand.setName(name);
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
    public void getBrandByName(String name, OnGetBrandByName callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        Brand brand = realmInstance.where(Brand.class).equalTo(RealmTable.NAME, name).findFirst();
        callback.call(brand);
    }

    @Override
    public void getAllBrands(OnGetAllBrandsCallback callback) {
        Realm realmInstance = Realm.getDefaultInstance();
        callback.onSuccess(realmInstance.where(Brand.class).findAll());
        realmInstance.close();
    }

    @Override
    public void deleteAllBrands() {
        Realm realmInstance = Realm.getDefaultInstance();
        final RealmResults<Brand> brands = realmInstance.where(Brand.class).findAll();
        realmInstance.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                brands.deleteAllFromRealm();
            }
        });
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
