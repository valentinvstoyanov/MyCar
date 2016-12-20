package stoyanov.valentin.mycar.app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import stoyanov.valentin.mycar.R;
import stoyanov.valentin.mycar.realm.models.Brand;
import stoyanov.valentin.mycar.realm.models.VehicleType;
import stoyanov.valentin.mycar.utils.CsvUtils;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                /*.initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                    }
                })*/
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
