package stoyanov.valentin.mycar.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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
