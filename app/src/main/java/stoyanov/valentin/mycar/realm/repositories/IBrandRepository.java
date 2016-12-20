package stoyanov.valentin.mycar.realm.repositories;

import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.Brand;

public interface IBrandRepository {

    interface OnAddOrGetBrandCallback {
        void onSuccess(Brand brand);
    }

    interface OnAddBrandListCallback {
        void onSuccess();
        void onError();
    }

    interface OnGetBrandByName {
        void call(Brand brand);
    }

    interface OnGetAllBrandsCallback {
        void onSuccess(RealmResults<Brand> callback);
    }

    void addBrand(String name);
    void addOrGetBrand(String name, OnAddOrGetBrandCallback callback);
    void addManyBrands(String[] names, OnAddBrandListCallback callback);
    void getBrandByName(String name, OnGetBrandByName callback);
    void deleteAllBrands();
    void getAllBrands(OnGetAllBrandsCallback callback);
}
