package stoyanov.valentin.mycar.realm.repositories;

import java.util.ArrayList;
import io.realm.RealmResults;
import stoyanov.valentin.mycar.realm.models.Brand;

public interface IBrandRepository {

    interface OnAddBrandListCallback {
        void onSuccess();
        void onError();
    }

    interface OnGetAllBrandsCallback {
        void onSuccess(RealmResults<Brand> callback);
    }

    void addBrand(String name);
    void addManyBrands(String[] names, OnAddBrandListCallback callback);
    void getAllBrands(OnGetAllBrandsCallback callback);
}
